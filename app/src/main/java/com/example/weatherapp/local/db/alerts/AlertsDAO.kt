package com.example.weatherapp.local.db.alerts


import androidx.room.*
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI

@Dao
interface AlertsDAO {

    @Query("SELECT * From weather")
    suspend fun getAlerts(): AlertModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlerts(weather: AlertModel)

    @Query("DELETE From weather")
    suspend fun deleteAlerts()
}