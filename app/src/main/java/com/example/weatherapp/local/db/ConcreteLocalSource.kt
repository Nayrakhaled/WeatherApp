package com.example.roomdemomvvm.db

import android.content.Context
import com.example.weatherapp.local.db.alerts.AlertsDAO
import com.example.weatherapp.local.db.alerts.AlertsDataBase
import com.example.weatherapp.local.db.favourite.FavDAO
import com.example.weatherapp.local.db.favourite.FavDataBase
import com.example.weatherapp.local.db.weather.AppDataBase
import com.example.weatherapp.local.db.weather.WeatherDAO
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.Alerts

import com.example.weatherapp.model.WeatherAPI

class ConcreteLocalSource (context: Context) : LocalSource {
    private val dao: WeatherDAO?
    private val daoFV: FavDAO?
    private val daoAlert: AlertsDAO?

    init {
        val db: AppDataBase = AppDataBase.getInstance(context)
        dao = db.weatherDAO()
        val dbFav: FavDataBase = FavDataBase.getInstance(context)
        daoFV = dbFav.favDAO()
        val dbAlert: AlertsDataBase = AlertsDataBase.getInstance(context)
        daoAlert = dbAlert.alertsDAO()
    }

    override fun insertWeather(weather: WeatherAPI) {
        dao?.insertAll(weather)
    }

    override suspend fun getWeather(): WeatherAPI {
       return dao!!.getWeather()
    }


    override fun insertFav(weather: WeatherAPI) {
        daoFV?.insertFav(weather)
    }

    override suspend fun getAllFavWeather(): List<WeatherAPI> {
        return daoFV!!.getAllWeatherFav()
    }

    override suspend fun getFavWeather(city: String): WeatherAPI {
        return daoFV!!.getWeatherFav(city)
    }

    override suspend fun deleteFav(weather: WeatherAPI) {
        daoFV?.deleteFav(weather.timezone)
    }

    override fun insertAlerts(alert: AlertModel) {
        daoAlert?.insertAlerts(alert);
    }

    override suspend fun getAlerts(): AlertModel {
        return daoAlert!!.getAlerts()
    }

    override suspend fun deleteAlerts() {
        daoAlert!!.deleteAlerts()
    }


}