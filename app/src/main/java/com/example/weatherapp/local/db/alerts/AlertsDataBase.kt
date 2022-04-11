package com.example.weatherapp.local.db.alerts

import android.content.Context
import androidx.room.*
import com.example.weatherapp.model.AlertModel
import kotlin.jvm.Synchronized
import com.example.weatherapp.model.DataBaseConvert
import com.example.weatherapp.model.WeatherAPI

@Database(entities = [AlertModel::class], version = 1)
@TypeConverters(DataBaseConvert::class)
abstract class AlertsDataBase : RoomDatabase() {
    abstract fun alertsDAO(): AlertsDAO

    companion object {
        private var INSTANCE: AlertsDataBase? = null
        //one thread at a time to access this method
        @Synchronized
        fun getInstance(context: Context): AlertsDataBase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AlertsDataBase::class.java,
                "alerts"
            ).fallbackToDestructiveMigration().build()
        }
    }
}