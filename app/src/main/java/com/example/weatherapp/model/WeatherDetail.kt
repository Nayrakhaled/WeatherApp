package com.example.weatherapp.model

data class Current(
    var dt: Long,
    var temp: Float,
    var pressure: Int,
    var humidity: Int,
    var clouds: Int,
    var uvi: Float,
    var weather: List<Weather>,
    var visibility: Int,
    var wind_speed: Float,
)

data class Weather(
    var main: String,
    var icon: String,
    var description: String
)

data class Temp(
    var min: Float,
    var max: Float
)

data class Daily(
    var dt: Long,
    var pressure: Int,
    var humidity: Int,
    var clouds: Int,
    var weather: List<Weather>,
    var uvi: Float,
    var temp: Temp
)

data class Hourly(
    var dt: Long,
    var temp: Float,
    var weather: List<Weather>
)