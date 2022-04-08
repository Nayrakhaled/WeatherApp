package com.example.weatherapp.setting.view_model

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository

class SettingViewModel(private val repo: Repository) : ViewModel() {

    private var _setting: MutableLiveData<SharedPreferences> = MutableLiveData()
    val setting: LiveData<SharedPreferences> = _setting


    fun saveSetting(keyName: String, valueName: Int){
        Log.i("TAG", "saveSetting: $valueName")
        repo.saveSettingInt(keyName, valueName)
    }

    fun getSetting(){
        Log.i("TAG", "saveSetting: ${repo.getSetting()}")
        _setting.postValue(repo.getSetting())
    }

}