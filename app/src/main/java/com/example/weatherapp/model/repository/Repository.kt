package com.example.weatherapp.model.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.roomdemomvvm.db.LocalSource
import com.example.weatherapp.local.sharedPrefs.SharedPrefsInterface
import com.example.weatherapp.model.Alerts
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.remote.RemoteSource

class Repository(
    private var remoteSource: RemoteSource,
    private var localSource: LocalSource,
    private var sharedPrefs: SharedPrefsInterface,
    var context: Context
) : RepositoryInterface {

    companion object {
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource, sharedPrefs: SharedPrefsInterface, context: Context): Repository {
            return instance ?: Repository(remoteSource, localSource, sharedPrefs, context)

        }
    }

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, lang: String,
        units: String
    ): WeatherAPI {
        return remoteSource.getCurrentWeather(lat, lon, lang, units)
    }

    override fun saveSetting(keyName: String, valueName: String) {
        sharedPrefs.saveSetting(keyName, valueName)
    }

    override fun saveSettingInt(keyName: String, valueName: Int) {
        sharedPrefs.saveSettingInt(keyName, valueName)
    }

    override fun getSetting(): SharedPreferences {
        return sharedPrefs.getSetting()
    }

    override fun insertWeather(weather: WeatherAPI) {
        localSource.insertWeather(weather)
    }

    override suspend fun getWeather(): WeatherAPI {
       return localSource.getWeather()
    }

    override fun insertFav(weather: WeatherAPI) {
        localSource.insertFav(weather)
    }

    override suspend fun getAllFavWeather(): List<WeatherAPI> {
        return localSource.getAllFavWeather()
    }

    override suspend fun getFavWeather(city: String): WeatherAPI {
        return localSource.getFavWeather(city)
    }

    override suspend fun deleteFav(weather: WeatherAPI) {
        localSource.deleteFav(weather)
    }
}