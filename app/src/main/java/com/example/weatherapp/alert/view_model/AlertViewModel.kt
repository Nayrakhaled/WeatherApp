package com.example.weatherapp.alert.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.Alerts
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlertViewModel(private val repo: Repository) : ViewModel() {


    private var _alertsAPI: MutableLiveData<AlertModel> = MutableLiveData()
    val alertsAPI: LiveData<AlertModel> = _alertsAPI


    fun insertAlert(alert: AlertModel){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlerts(alert)
        }
        getAlerts()
    }


     fun getAlerts(){
        viewModelScope.launch {
            val alert = repo.getAlerts()
            withContext(Dispatchers.Main) {
                _alertsAPI.postValue(alert)
            }
        }
    }


    fun deleteAlerts(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repo.deleteAlert()
            }
            getAlerts()
        }
    }


}