package com.example.weathertracker.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertracker.data.local.toEntity
import com.example.weathertracker.data.local.toWeatherResponse
import com.example.weathertracker.data.repository.WeatherRepository
import com.example.weathertracker.data.remote.WeatherResponse
import com.example.weathertracker.domain.model.WeatherContract
import com.example.weathertracker.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val networkUtils: NetworkUtils,
    application: Application,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherContract.ViewState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<WeatherContract.Action>()
    val actions = _actions.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun searchCity(query: String, isFromDatabase: Boolean = false) {
        Log.d("WeatherVM", "Starting search for city: $query")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            Log.d("WeatherVM", "Updated UI state to loading")

            if (networkUtils.isNetworkAvailable()) {
                try {
                    Log.d("WeatherVM", "Making API call for city: $query")
                    weatherRepository.getCurrentWeather(query)
                        .onSuccess { weather ->
                            Log.d("WeatherVM", "Successfully received weather data: $weather")
                            if (isFromDatabase) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        selectedWeather = weather,
                                        isError = false
                                    )
                                }
                                Log.d("WeatherVM", "Updated UI state with selected weather")
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        searchResult = weather,
                                        isError = false
                                    )
                                }
                                Log.d("WeatherVM", "Updated UI state with search result")
                            }
                        }
                        .onFailure { error ->
                            Log.e("WeatherVM", "Failed to fetch weather data", error)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    searchResult = null,
                                    isError = true
                                )
                            }
                            _actions.emit(WeatherContract.Action.ShowToast("City not found"))
                        }
                } catch (e: Exception) {
                    handleError(e)
                }
            } else {
                handleNoNetwork()
            }
        }
    }

    fun selectCity(weather: WeatherResponse) {
        viewModelScope.launch {
            try {
                Log.d("WeatherVM", "Selecting city: ${weather.location.name}")
                weatherRepository.saveWeather(weather.toEntity())
                _uiState.update {
                    it.copy(
                        selectedWeather = weather,
                        searchResult = null
                    )
                }
                _searchQuery.value = ""
                Log.d("WeatherVM", "City selected and saved successfully")
            } catch (e: Exception) {
                Log.e("WeatherVM", "Error selecting city", e)
                _actions.emit(WeatherContract.Action.ShowToast("Error saving city"))
            }
        }
    }

    fun loadLastSavedCity() {
        viewModelScope.launch {
            try {
                Log.d("WeatherVM", "Loading last saved city")
                val lastCity = weatherRepository.getLastSavedCity()

                if (networkUtils.isNetworkAvailable()) {
                    lastCity?.let { city ->
                        searchCity(city.cityName, isFromDatabase = true)
                        Log.d("WeatherVM", "Fetching fresh data for: ${city.cityName}")
                    }
                } else {
                    lastCity?.let { city ->
                        _uiState.update {
                            it.copy(selectedWeather = city.toWeatherResponse())
                        }
                        _actions.emit(WeatherContract.Action.ShowToast("Offline: Showing saved data"))
                        Log.d("WeatherVM", "Offline mode - loaded cached data for: ${city.cityName}")
                    }
                }
            } catch (e: Exception) {
                Log.e("WeatherVM", "Error loading last saved city", e)
                _uiState.update { it.copy(isError = true) }
                _actions.emit(WeatherContract.Action.ShowToast("Error loading saved city"))
            }
        }
    }

    private fun handleError(e: Exception) {
        Log.e("WeatherVM", "Exception while searching city", e)
        _uiState.update {
            it.copy(
                isLoading = false,
                isError = true,
                searchResult = null
            )
        }
        _actions.tryEmit(WeatherContract.Action.ShowToast(e.message ?: "Error searching city"))
    }

    private fun handleNoNetwork() {
        _uiState.update {
            it.copy(
                isLoading = false,
                isError = true
            )
        }
        _actions.tryEmit(WeatherContract.Action.ShowToast("No internet connection"))
    }




}
