package com.example.weatherapp.remote

import com.example.weatherapp.model.WeatherAPI

interface RemoteSource {

    suspend fun getCurrentWeather( lat: Float, lon: Float, exclude: String, appid: String): WeatherAPI
}