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
    private lateinit var locationRequest: LocationRequest


    @SuppressLint("MissingPermission")
     fun getLastLocation() {
        mFusedLocation.lastLocation.addOnCompleteListener { task ->
            val location: Location? = task.result
            if (location == null) {
                getNewLocation()
            } else _location.postValue(LatLng(location.latitude, location.longitude))

        }
    }

    fun getCity(loc: LatLng): String{
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
        return addresses[0].featureName
    }

    private fun getNewLocation() {
        mFusedLocation = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location = p0.lastLocation
            Log.i("TAG", "onLocationResult: ${lastLocation.longitude}")
        }
    }

}