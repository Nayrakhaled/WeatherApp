package com.example.weatherapp.home.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repository.Repository
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("This Class Could not br found")
        }
    }

}