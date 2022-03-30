package com.example.weatherapp.home.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private var _currentWeather: MutableLiveData<WeatherAPI> = MutableLiveData()
    val currentWeather: LiveData<WeatherAPI> = _currentWeather

    fun getCurrentWeather(lat: Float, lon: Float, exclude: String, appid: String) {
        viewModelScope.launch {
           val current =  repo.getCurrentWeather(lat, lon, exclude, appid)
            withContext(Dispatchers.IO){
                _currentWeather.postValue(current)
            }
        }
    }




}