package com.example.weatherapp.alert.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Alerts
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlertViewModel(private val repo: Repository) : ViewModel() {


    private var _alertsAPI: MutableLiveData<WeatherAPI> = MutableLiveData()
    val alertsAPI: LiveData<WeatherAPI> = _alertsAPI


    fun getAlertsAPI(){
        viewModelScope.launch {
            val alert = repo.getWeather()
            withContext(Dispatchers.Main) {
                _alertsAPI.postValue(alert)
            }
        }
    }


}