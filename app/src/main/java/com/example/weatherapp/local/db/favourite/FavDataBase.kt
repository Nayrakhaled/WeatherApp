package com.example.weatherapp.local.db.favourite

import android.content.Context
import androidx.room.*
import kotlin.jvm.Synchronized
import com.example.weatherapp.model.DataBaseConvert
import com.example.weatherapp.model.WeatherAPI

@Database(entities = [WeatherAPI::class], version = 1)
@TypeConverters(DataBaseConvert::class)
abstract class FavDataBase : RoomDatabase() {
    abstract fun favDAO(): FavDAO

    companion object {
        private var INSTANCE: FavDataBase? = null
        //one thread at a time to access this method
        @Synchronized
        fun getInstance(context: Context): FavDataBase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                FavDataBase::class.java,
                "favourite"
            ).fallbackToDestructiveMigration().build()
        }
    }
}