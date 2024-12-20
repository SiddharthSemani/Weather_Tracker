package com.example.weathertracker.data.local

import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE cityName = :cityName")
    suspend fun getWeatherForCity(cityName: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastSavedCity(): WeatherEntity?
}
