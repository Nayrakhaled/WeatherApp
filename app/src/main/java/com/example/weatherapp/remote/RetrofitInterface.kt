package com.example.mvvm.network


import com.example.weatherapp.model.WeatherAPI
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("onecall?")
    suspend fun getCurrentWeather(
    @Query("lat") lat: Float,
    @Query("lon") lon: Float,
    @Query("exclude") exclude: String,
    @Query("appid") app_id: String): WeatherAPI
}