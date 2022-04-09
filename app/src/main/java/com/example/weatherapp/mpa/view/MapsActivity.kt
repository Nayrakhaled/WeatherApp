package com.example.weatherapp.mpa.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.*
import com.example.weatherapp.databinding.ActivityMapsBinding
import com.example.weatherapp.favourite.view.FavouriteFragment
import com.example.weatherapp.home.view.HomeFragment
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.mpa.view_model.MapViewModel
import com.example.weatherapp.mpa.view_model.MapViewModelFactory
import com.example.weatherapp.remote.WeatherClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: MapViewModelFactory

    //egypt
    private var currentLocation: LatLng = LatLng(30.0595581, 31.223445)
    private lateinit var geocoder: Geocoder
    private lateinit var marker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getInit()

        binding.btnConfirm.setOnClickListener {
            Log.i("TAG", "onCreate: ${currentLocation.latitude}")
            when {
                intent.extras?.get("init")?.equals("home") == true -> {
                    Log.i("TAG", "onCreate: MAP Activity")
                    mapViewModel.saveSetting("lat", currentLocation.latitude.toString())
                    mapViewModel.saveSetting("log", currentLocation.longitude.toString())
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                intent.extras?.get("favourite")?.equals("fav") == true -> {
                    Log.i("TAG", "onCreate: Fav")
                    mapViewModel.getDataFromShared()
                    mapViewModel.data.observe(this) {
                        val lang = when {
                            it.getInt("Language", -1) == 1 -> "ar"
                            else -> "en"
                        }
                        val temp = when {
                            it.getInt("Temp", -1) == 2 -> IMPERIAL
                            it.getInt("Temp", -1) == 1 -> METRIX
                            else -> DEFAULT
                        }
                        mapViewModel.getCurrentWeather(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            lang, temp
                        )
                        HomeFragment.flag = 1
                    }
                }
                else -> {
                    Log.i("TAG", "onCreate: ${currentLocation.latitude}")
                    mapViewModel.saveSetting("lat", currentLocation.latitude.toString())
                    mapViewModel.saveSetting("log", currentLocation.longitude.toString())
                }
            }
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        if (HomeFragment.isOnline(this)) {
            Log.i("TAG", "onMapReady: online")
            binding.btnConfirm.isVisible = true
            mMap = googleMap
            // Add a marker in Sydney and move the camera
            marker = mMap.addMarker(
                MarkerOptions().position(currentLocation)
                    .draggable(true)
            )!!
            marker.showInfoWindow()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))


            // marker
            mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDrag(location: Marker) {

                }

                override fun onMarkerDragEnd(location: Marker) {
                    currentLocation =
                        LatLng(location.position.latitude, location.position.longitude)
                    Log.i("TAG", "onMarkerDragEnd: ${location.position.longitude}")
//                val address = getAddress(
//                    geocoder
//                        .getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
//                )
//                marker.title = address
                    marker.showInfoWindow()
                }

                override fun onMarkerDragStart(p0: Marker) {
                    marker.title = ""
                }

            })


            //map
            mMap.setOnMapClickListener { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                Log.i("TAG", "onMapReady: ${location.latitude}")
                marker.remove()
                marker = mMap.addMarker(MarkerOptions().position(currentLocation).draggable(true))!!

                Log.i("TAG", "onMapReady: ${currentLocation.longitude}")
//            marker.title = address
                marker.showInfoWindow()
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100
                )
                return
            }
        } else {
            Log.i("TAG", "onMapReady: offline")
            binding.btnConfirm.isVisible = false
            Toast.makeText(this, "No Internet", Toast.LENGTH_LONG)
        }

    }

    private fun getInit() {
        geocoder = Geocoder(this, Locale.getDefault())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(this),
                SharedPrefs.getInstance(this),
                this
            )
        )

        mapViewModel = ViewModelProvider(this, mapViewModelFactory)[MapViewModel::class.java]

    }
}