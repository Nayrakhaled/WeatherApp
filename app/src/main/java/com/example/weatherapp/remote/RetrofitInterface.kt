package com.example.mvvm.network


import com.example.weatherapp.API_KEY_WEATHER
import com.example.weatherapp.model.WeatherAPI
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("onecall?appid=$API_KEY_WEATHER")
    suspend fun getCurrentWeather(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double,
    @Query("lang") lang: String,
    @Query("units") units: String
    ): WeatherAPI
}