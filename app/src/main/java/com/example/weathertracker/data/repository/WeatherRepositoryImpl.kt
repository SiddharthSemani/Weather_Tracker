package com.example.weathertracker.data.repository

import android.util.Log
import com.example.weathertracker.data.local.WeatherDao
import com.example.weathertracker.data.local.WeatherEntity
import com.example.weathertracker.data.local.toEntity
import com.example.weathertracker.data.remote.WeatherApiService
import com.example.weathertracker.data.remote.WeatherResponse
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String): Result<WeatherResponse> {
        return try {
            Log.d("WeatherRepo", "Fetching weather for city: $city")
            val response = api.getCurrentWeather(query = city)
            Log.d("WeatherRepo", "Weather data received: $response")
            saveWeather(response.toEntity())
            Result.success(response)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error fetching weather: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getLastSavedCity(): WeatherEntity? {
        return weatherDao.getLastSavedCity()
    }

    override suspend fun saveWeather(weather: WeatherEntity) {
        weatherDao.insertWeather(weather)
    }
}