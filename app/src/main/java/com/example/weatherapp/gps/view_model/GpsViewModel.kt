package com.example.weatherapp.gps.view_model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.util.*


class GpsViewModel(var context: Context) : ViewModel() {

    private val _location: MutableLiveData<LatLng> = MutableLiveData()
    val location: LiveData<LatLng> = _location
    private var mFusedLocation: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
     fun getLastLocation() {
        mFusedLocation.lastLocation.addOnSuccessListener {

            if (it == null) {
                getNewLocation()
            } else _location.postValue(LatLng(it.latitude, it.longitude))

        }
    }

    fun getCity(loc: LatLng): String{
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
        return addresses[0].countryName
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        mFusedLocation = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback : LocationCallback = object : LocationCallback(){
            override fun onLocationResult(onLocalResult: LocationResult?) {
                super.onLocationResult(onLocalResult)
                val loc : Location = onLocalResult!!.lastLocation
                _location.postValue(LatLng(loc.latitude,loc.longitude))
            }
        }
        mFusedLocation.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
}