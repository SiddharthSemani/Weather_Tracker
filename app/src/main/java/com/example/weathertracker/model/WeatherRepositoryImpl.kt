package com.example.weathertracker.model

import com.example.weathertracker.data.local.WeatherEntity
import com.example.weathertracker.data.remote.WeatherResponse

private fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = location.name,
        temperature = current.tempC,
        feelsLike = current.feelsLikeC,
        humidity = current.humidity,
        uvIndex = current.uv,
        conditionText = current.condition.text,
        conditionIcon = current.condition.icon
    )
}
