package com.example.weathertracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weathertracker.data.remote.Condition
import com.example.weathertracker.data.remote.Current
import com.example.weathertracker.data.remote.Location
import com.example.weathertracker.data.remote.WeatherResponse

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val cityName: String,
    val temperature: Float,
    val feelsLike: Float,
    val humidity: Int,
    val uvIndex: Float,
    val conditionText: String,
    val conditionIcon: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun WeatherEntity.toWeatherResponse(): WeatherResponse {
    // Implement conversion from Entity to Response
    // This is a placeholder - implement actual conversion based on your data models
    return WeatherResponse(
        location = Location(name = cityName, region = "", country = "", lat = 0.0, lon = 0.0, localtime = ""),
        current = Current(
            tempC = temperature,
            condition = Condition(text = conditionText, icon = conditionIcon),
            humidity = humidity,
            uv = uvIndex,
            feelsLikeC = feelsLike
        )
    )
}

fun WeatherResponse.toEntity(): WeatherEntity {
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
