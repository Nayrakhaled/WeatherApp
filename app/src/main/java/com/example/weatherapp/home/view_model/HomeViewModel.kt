package com.example.weatherapp.home.view_model

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private var _currentWeather: MutableLiveData<WeatherAPI> = MutableLiveData()
    val currentWeather: LiveData<WeatherAPI> = _currentWeather

    private var _data: MutableLiveData<SharedPreferences> = MutableLiveData()
    val data: LiveData<SharedPreferences> = _data

    fun getCurrentWeather(
        lat: Double, lon: Double, lang: String,
        units: String
    ) {
        viewModelScope.launch {
            val current = repo.getCurrentWeather(lat, lon,  lang, units)
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(current)
                insertWeather(current)
            }
        }
    }
    fun getDataFromShared(){
        _data.postValue(repo.getSetting())
    }

    private fun insertWeather(weather: WeatherAPI) {
        viewModelScope.launch(Dispatchers.IO){
            repo.insertWeather(weather)
        }
    }

    fun saveSetting(keyName: String, valueName: String){
        Log.i("TAG", "saveSetting: $valueName")
        repo.saveSetting(keyName, valueName)
    }

     fun getWeather(){
        viewModelScope.launch {
            val current = repo.getWeather()
            withContext(Dispatchers.Main) {
                _currentWeather.postValue(current)
            }
        }
    }
}