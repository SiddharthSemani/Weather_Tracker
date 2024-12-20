package com.example.weathertracker.domain.model

import com.example.weathertracker.data.remote.WeatherResponse

interface WeatherContract {
    data class ViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val searchQuery: String = "",
        val searchResult: WeatherResponse? = null,
        val selectedWeather: WeatherResponse? = null
    ) {
        val refinedViewState = when {
            isLoading -> RefinedViewState.Loading
            isError -> RefinedViewState.Error
            searchResult != null -> RefinedViewState.SearchResult(searchResult)
            selectedWeather != null -> RefinedViewState.WeatherDetail(selectedWeather)
            else -> RefinedViewState.Empty
        }
    }

    sealed class Action {
        data class ShowToast(val message: String) : Action()
        data class SaveCity(val city: String) : Action()
    }
}

sealed interface RefinedViewState {
    object Loading : RefinedViewState
    object Error : RefinedViewState
    object Empty : RefinedViewState
    data class SearchResult(val weather: WeatherResponse) : RefinedViewState
    data class WeatherDetail(val weather: WeatherResponse) : RefinedViewState
}
