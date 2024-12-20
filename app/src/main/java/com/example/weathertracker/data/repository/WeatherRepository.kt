package com.example.weathertracker.data.repository

import com.example.weathertracker.data.local.WeatherEntity
import com.example.weathertracker.data.remote.WeatherResponse

interface WeatherRepository {
    suspend fun getCurrentWeather(city: String): Result<WeatherResponse>
    suspend fun getLastSavedCity(): WeatherEntity?
    suspend fun saveWeather(weather: WeatherEntity)
}
