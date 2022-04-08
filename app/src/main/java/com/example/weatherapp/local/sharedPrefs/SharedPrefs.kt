package com.example.weatherapp.local.sharedPrefs

import android.app.Activity
import android.content.Context

import android.content.SharedPreferences
import com.example.weatherapp.SHARED_NAME


class SharedPrefs private constructor() : SharedPrefsInterface {
    companion object {
        private var sharedPref: SharedPrefs = SharedPrefs()
        private lateinit var sharedPreferences: SharedPreferences
        private var editor: SharedPreferences.Editor? = null


        fun getInstance(context: Context): SharedPrefs {
            if (!::sharedPreferences.isInitialized) {
                synchronized(SharedPrefs::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        sharedPreferences = context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE)
                        editor =  sharedPreferences.edit()
                    }
                }
            }
            return sharedPref
        }
    }

    override fun saveSetting(keyName: String, valueName: String) {
        editor!!.putString(keyName, valueName)
        editor!!.commit()
    }

    override fun saveSettingInt(keyName: String, valueName: Int) {
        editor!!.putInt(keyName, valueName)
        editor!!.commit()
    }


    override fun getSetting(): SharedPreferences {
        return sharedPreferences
    }
}