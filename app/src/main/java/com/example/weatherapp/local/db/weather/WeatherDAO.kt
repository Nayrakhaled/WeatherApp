package com.example.weatherapp.local.db.weather


import androidx.room.*
import com.example.weatherapp.model.Alerts
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI
import org.jetbrains.annotations.Nullable

@Dao
interface WeatherDAO {
    @Query("SELECT * From weather")
    suspend fun getWeather(): WeatherAPI

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(weather: WeatherAPI)

    @Delete
    fun delete(weather: WeatherAPI)
}