package com.example.weatherapp.remote

import com.example.mvvm.network.RetrofitClient
import com.example.mvvm.network.RetrofitInterface
import com.example.weatherapp.model.WeatherAPI

class WeatherClient private constructor(): RemoteSource {

    companion object{
        private var instance: WeatherClient? = null

        fun getInstance(): WeatherClient {
            return instance?: WeatherClient()
        }
    }

    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        exclude: String,
        appid: String
    ): WeatherAPI {
        val weatherService = RetrofitClient.getClient()?.create(RetrofitInterface::class.java)
        return weatherService!!.getCurrentWeather(lat, lon, exclude, appid)
    }


}