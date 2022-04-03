package com.example.weatherapp.local.db.favourite


import androidx.room.*
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI

@Dao
interface FavDAO {
    @Query("SELECT * From weather WHERE timezone LIKE :city")
    suspend fun getWeatherFav(city: String): WeatherAPI

    @Query("SELECT * From weather")
    suspend fun getAllWeatherFav(): List<WeatherAPI>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFav(weather: WeatherAPI)

    @Query("DELETE From weather WHERE timezone LIKE :city")
    fun deleteFav(city: String)
}