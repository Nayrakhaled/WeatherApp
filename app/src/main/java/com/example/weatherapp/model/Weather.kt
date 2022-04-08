package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather")
data class WeatherAPI(
    var lat: Double,
    var lon: Double,
    @PrimaryKey
    var timezone: String,
    var current: Current,
    var daily: List<Daily>,
    var hourly: List<Hourly>,
    var alerts: List<Alerts>?,
)

