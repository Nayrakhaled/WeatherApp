package com.example.weatherapp.mpa.view

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMapsBinding
import com.example.weatherapp.favourite.view.FavouriteFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //egypt
    private var currentLocation: LatLng = LatLng(30.0595581, 31.223445)
    private lateinit var geocoder: Geocoder
    private lateinit var marker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        geocoder = Geocoder(this, Locale.getDefault())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnConfirm.setOnClickListener {
            Log.i("TAG", "onCreate: ${currentLocation.latitude}")
            latLng = currentLocation
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        marker = mMap.addMarker(
            MarkerOptions().position(sydney)
                .draggable(true).title("Marker in Sydney")
        )!!
        marker.showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        // marker
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(location: Marker) {

            }

            override fun onMarkerDragEnd(location: Marker) {
                currentLocation = LatLng(location.position.latitude, location.position.longitude)
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

//            val address = getAddress(
//                geocoder
//                    .getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
//            )

            Log.i("TAG", "onMapReady: ${currentLocation.longitude}")
//            marker.title = address
            marker.showInfoWindow()
        }
    }

    private fun getAddress(address: List<Address>): String? {
        var countryName: String? = null
        if (!address.isNullOrEmpty()) {
            countryName = address[0].countryName
        }
        return countryName
    }

    companion object {
        var latLng: LatLng = LatLng(0.0, 0.0)
    }
}