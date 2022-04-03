package com.example.weatherapp.setting.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repository.Repository
import java.lang.IllegalArgumentException

class SettingViewModelFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewModel::class.java)){
            SettingViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("This Class Could not br found")
        }
    }

}