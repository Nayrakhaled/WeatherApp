package com.example.weatherapp.gps.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class GpsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GpsViewModel::class.java)){
            GpsViewModel(context) as T
        }
        else{
            throw IllegalArgumentException("This Class Could not br found")
        }
    }
}