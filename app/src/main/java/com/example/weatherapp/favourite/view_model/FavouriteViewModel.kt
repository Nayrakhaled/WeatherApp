package com.example.weatherapp.favourite.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(private val repo: Repository) : ViewModel() {

    private var _weather: MutableLiveData<WeatherAPI> = MutableLiveData()
    val weather: LiveData<WeatherAPI> = _weather

    private var _favWeather: MutableLiveData<List<WeatherAPI>> = MutableLiveData()
    val favWeather: LiveData<List<WeatherAPI>> = _favWeather



    fun getAllFavWeather(){
        viewModelScope.launch {
            val fav = repo.getAllFavWeather()
            withContext(Dispatchers.Main) {
                _favWeather.postValue(fav)
            }
        }
    }

    fun deleteFavWeather(weatherAPI: WeatherAPI){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repo.deleteFav(weatherAPI)
            }
           getAllFavWeather()
        }
    }

}