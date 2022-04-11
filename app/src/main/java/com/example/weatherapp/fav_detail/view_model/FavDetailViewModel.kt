package com.example.weatherapp.fav_detail.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavDetailViewModel (private val repo: Repository) : ViewModel() {

    private var _weatherFav: MutableLiveData<WeatherAPI> = MutableLiveData()
    val weatherFav: LiveData<WeatherAPI> = _weatherFav


    fun getFavDetailWeather(city: String){
        viewModelScope.launch {
            val fav = repo.getFavWeather(city)
            withContext(Dispatchers.Main) {
                _weatherFav.postValue(fav)
            }
        }
    }

}