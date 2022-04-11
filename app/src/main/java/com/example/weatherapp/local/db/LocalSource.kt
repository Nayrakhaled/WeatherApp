package com.example.roomdemomvvm.db

import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.Alerts
import com.example.weatherapp.model.WeatherAPI

interface LocalSource {
    fun insertWeather(weather: WeatherAPI)
//    fun deleteMovie(movie: Movie)
    suspend fun getWeather(): WeatherAPI


    fun insertFav(weather: WeatherAPI)
    suspend fun getAllFavWeather(): List<WeatherAPI>
    suspend fun getFavWeather(city: String): WeatherAPI
    suspend fun deleteFav(weather: WeatherAPI)

    fun insertAlerts(alert: AlertModel)
    suspend fun getAlerts(): AlertModel
    suspend fun deleteAlerts()
}