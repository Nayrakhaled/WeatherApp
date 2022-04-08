package com.example.weatherapp.model.repository

import android.content.SharedPreferences
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI

interface RepositoryInterface {

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): WeatherAPI

    //SharedPrefs
    fun saveSetting(keyName: String, valueName: String)
    fun saveSettingInt(keyName: String, valueName: Int)
    fun getSetting(): SharedPreferences

    //Room Weather
    fun insertWeather(weather: WeatherAPI)
    suspend fun getWeather(): WeatherAPI

    //Room Fav
    fun insertFav(weather: WeatherAPI)
    suspend fun getAllFavWeather(): List<WeatherAPI>
    suspend fun getFavWeather(city: String): WeatherAPI
    suspend fun deleteFav(weather: WeatherAPI)

}