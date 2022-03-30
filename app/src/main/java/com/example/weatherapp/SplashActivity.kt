package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weatherapp.databinding.ActivitySplushBinding
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*




class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplushBinding
    private lateinit var mFusedLocation: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplushBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)

        binding.btn.setOnClickListener {
             //getLastLocation()
            showDialog()
        }
    }


    private fun checkPermission(): Boolean {
        //check the location permissions and return true or false.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID_GPS
        )
    }

    private fun isLocationEnabled():Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                mFusedLocation.lastLocation.addOnCompleteListener{ task ->
                    val location: Location? = task.result
                    if(location == null){
                        getNewLocation()
                    }else Log.i("TAG", "getLastLocation: ${location.latitude} ${location.longitude}")
                }
            }else Log.i("TAG", "getLastLocation: no permission")
        }else requestPermission()
    }

    private fun getNewLocation(){
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(5000)
        locationRequest.setNumUpdates(1)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                Toast.makeText(
                    this@SplashActivity,
                    "Latitude: " + location.latitude,
                    Toast.LENGTH_LONG
                ).show()
                Toast.makeText(
                    this@SplashActivity,
                    "Longitude: " + location.longitude,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocation!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location = p0.lastLocation
            Log.i("TAG", "onLocationResult: ${lastLocation.longitude}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID_GPS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("TAG", "onRequestPermissionsResult: have Permission")
            }
        }
    }


    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_screen,null)
        val  okBtn = view.findViewById<Button>(R.id.ok_btn)
        val  radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_loc)
        builder.setView(view)
        okBtn.setOnClickListener {
            val selectedOption: Int = radioGroup!!.checkedRadioButtonId
            radioButton = view.findViewById(selectedOption)
            Log.i("TAG", "showDialog: ${radioButton.text}")
            if(radioButton.text == "Map") startActivity(Intent(applicationContext, MapActivity::class.java))
                else Log.i("TAG", "showDialog: GPS")
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}


