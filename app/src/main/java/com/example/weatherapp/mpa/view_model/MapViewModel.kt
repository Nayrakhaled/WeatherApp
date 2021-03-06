package com.example.weatherapp.mpa.view_model

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

class MapViewModel(private val repo: Repository) : ViewModel() {

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
            withContext(Dispatchers.IO) {
                insertFavWeather(current)
                Log.i("TAG", "getCurrentWeather: ${current.timezone}")
            }
        }
    }

    fun saveSetting(keyName: String, valueName: String){
        Log.i("TAG", "saveSetting: $valueName")
        repo.saveSetting(keyName, valueName)
    }

    fun saveSettingInt(keyName: String, valueName: Int){
        Log.i("TAG", "saveSetting: $valueName")
        repo.saveSettingInt(keyName, valueName)
    }

    fun getDataFromShared(){
        _data.postValue(repo.getSetting())
    }

    private fun insertFavWeather(weather: WeatherAPI) {
            repo.insertFav(weather)
    }

}