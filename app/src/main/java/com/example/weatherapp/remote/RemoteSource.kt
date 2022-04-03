package com.example.weatherapp.remote

import com.example.weatherapp.model.WeatherAPI

interface RemoteSource {

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): WeatherAPI
}