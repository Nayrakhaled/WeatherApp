package com.example.weatherapp.fav_detail.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.favourite.view_model.FavouriteViewModel
import com.example.weatherapp.model.repository.Repository
import java.lang.IllegalArgumentException

class FavDetailViewModelFactory (private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavDetailViewModel::class.java)){
            FavDetailViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("This Class Could not br found")
        }
    }

}