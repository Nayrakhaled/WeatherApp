package com.example.weatherapp.local.db.weather


import androidx.room.*
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI

@Dao
interface WeatherDAO {
    @Query("SELECT * From weather")
    suspend fun getWeather(): WeatherAPI

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(weather: WeatherAPI)

    @Delete
    fun delete(weather: WeatherAPI)
}