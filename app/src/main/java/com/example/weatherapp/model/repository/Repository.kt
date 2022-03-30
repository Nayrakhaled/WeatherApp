package com.example.weatherapp.model.repository

import android.content.Context
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.remote.RemoteSource

class Repository(
    private var remoteSource: RemoteSource,
    var context: Context
    ): RepositoryInterface {

    companion object{
        private var instance:Repository?=null
        fun getInstance(remoteSource:RemoteSource , context: Context):Repository{
            return instance?: Repository(remoteSource, context)

        }
    }

    override suspend fun getCurrentWeather(lat: Float, lon: Float, exclude: String, appid: String): WeatherAPI {
        return remoteSource.getCurrentWeather(lat, lon, exclude, appid)
    }


}