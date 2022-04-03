package com.example.roomdemomvvm.db

import android.content.Context
import com.example.weatherapp.local.db.favourite.FavDAO
import com.example.weatherapp.local.db.favourite.FavDataBase
import com.example.weatherapp.local.db.weather.AppDataBase
import com.example.weatherapp.local.db.weather.WeatherDAO

import com.example.weatherapp.model.WeatherAPI

class ConcreteLocalSource (context: Context) : LocalSource {
    private val dao: WeatherDAO?
    private val daoFV: FavDAO?

    init {
        val db: AppDataBase = AppDataBase.getInstance(context)
        dao = db.weatherDAO()
        val dbFav: FavDataBase = FavDataBase.getInstance(context)
        daoFV = dbFav.favDAO()
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

//    override fun deleteMovie(movie: Movie) {
//        //Thread { dao?.delete(movie) }.start()
//        dao?.delete(movie)
//    }

}