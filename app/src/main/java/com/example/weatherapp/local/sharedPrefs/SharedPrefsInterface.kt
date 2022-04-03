package com.example.weatherapp.local.sharedPrefs

import android.content.SharedPreferences

interface SharedPrefsInterface {

    fun saveSetting(keyName: String, valueName: String)

    fun getSetting(): SharedPreferences
}