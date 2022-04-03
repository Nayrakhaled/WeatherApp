package com.example.weatherapp.local.db.weather

import android.content.Context
import androidx.room.*
import kotlin.jvm.Synchronized
import com.example.weatherapp.model.DataBaseConvert
import com.example.weatherapp.model.WeatherAPI

@Database(entities = [WeatherAPI::class], version = 1)
@TypeConverters(DataBaseConvert::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun weatherDAO(): WeatherDAO

    companion object {
        private var INSTANCE: AppDataBase? = null
        //one thread at a time to access this method
        @Synchronized
        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "weathers"
            ).fallbackToDestructiveMigration().build()
        }
    }
}