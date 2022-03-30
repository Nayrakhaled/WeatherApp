package com.example.weatherapp.model.repository

import com.example.weatherapp.model.WeatherAPI

interface RepositoryInterface {

    suspend fun getCurrentWeather(lat: Float, lon: Float, exclude: String, appid: String): WeatherAPI
}