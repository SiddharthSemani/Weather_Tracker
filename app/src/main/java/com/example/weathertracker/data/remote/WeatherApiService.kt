package com.example.weathertracker.data.remote

import com.example.weathertracker.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Query("q") query: String,
    ): WeatherResponse
}


