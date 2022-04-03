package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters


@Entity(tableName = "weather")
data class WeatherAPI(
    var lat: Float,
    var lon: Float,
    @PrimaryKey
    var timezone: String,
    var current: Current,
    var daily: List<Daily>,
    var hourly: List<Hourly>,
)

