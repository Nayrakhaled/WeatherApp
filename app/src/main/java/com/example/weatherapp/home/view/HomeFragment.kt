package com.example.weatherapp.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.*
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.gps.view_model.GpsViewModel
import com.example.weatherapp.gps.view_model.GpsViewModelFactory
import com.example.weatherapp.home.view.adapter.DaysAdapter
import com.example.weatherapp.home.view.adapter.HoursAdapter
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var gpsViewModel: GpsViewModel
    private lateinit var gpsViewModelFactory: GpsViewModelFactory
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private val binding get() = _binding!!
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var language: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        getInit()

        homeViewModel.getDataFromShared()
        homeViewModel.data.observe(viewLifecycleOwner) {
            val lang = when {
                it.getInt("Language", -1) == 1 -> "ar"
                else -> "en"
            }
            language = lang
            val temp = when {
                it.getInt("Temp", -1) == 2 -> IMPERIAL
                it.getInt("Temp", -1) == 1 -> METRIX
                else -> DEFAULT
            }

            if (isOnline(requireContext())) {
                Log.i("TAG", "onCreateView: online $lang")
                if (it.getInt("Location", -1) == 1) {
                    Log.i("TAG", "onCreateView: ${it.getString("lat", null)!!.toDouble()}")

                    homeViewModel.getCurrentWeather(
                        it.getString("lat", null)!!.toDouble(),
                        it.getString("log", null)!!.toDouble(),
                        lang,
                        temp
                    )
                } else {
                    Log.i("TAG", "onCreateView: Gps")
                    gpsViewModel.getLastLocation()

                    gpsViewModel.location.observe(viewLifecycleOwner) { loc ->
                        Log.i("TAG", "onCreateView: ${loc.latitude}")
                        homeViewModel.getCurrentWeather(
                            loc.latitude,
                            loc.longitude, lang, temp
                        )
                    }
                }

            } else {
                homeViewModel.getWeather()
            }
        }

        homeViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            binding.txtCity.text = String.format(Locale(language), "%s", gpsViewModel.getCity(LatLng(weather.lat, weather.lon)))
            binding.txtDate.text =
                convertUTCToLocalDate(weather.current.dt, "EEE, dd MMM", language)
            binding.txtDesc.text = weather.current.weather[0].description
//            Glide.with(requireContext())
//                .load(ICON_URL + weather.current.weather[0].icon + EXTENDED_IMG)
//                .override(150, 70)
//                .into(binding.imgCurrent)

            homeViewModel.data.observe(viewLifecycleOwner) {
//                Log.i("TAG", "onCreateView: ${it.getString("Temp", null)}")
                val temp = when {
                    it.getInt("Temp", -1) == 2 -> FAHRENHEIT
                    it.getInt("Temp", -1) == 1 -> CELSIUS
                    else -> KELVIN
                }
                binding.txtDegree.text =
                    String.format(Locale(language), "%d", weather.current.temp.toInt())
                binding.txtTypeDegree.text = getString(temp)

                daysAdapter.setDaysList(weather.daily, getString(temp), language)
                daysAdapter.notifyDataSetChanged()

                hoursAdapter.setHoursList(weather.hourly, getString(temp), language)
                hoursAdapter.notifyDataSetChanged()

                val speed = when {
                    it.getInt("Speed", -1) == 1 -> MILE_HOUR
                    else -> METER_SEC
                }
                Log.i("TAG", "onCreateView:Speed $speed")
                binding.txtWind.text = ""
                binding.txtWind.text = "${
                    String.format(
                        Locale(language),
                        "%d",
                        weather.current.wind_speed.toInt()
                    )
                } $speed"

                binding.txtPressure.text = "${
                    String.format(
                        Locale(language),
                        "%d",
                        weather.current.pressure.toInt()
                    )
                } ${getString(R.string.hpa)}"

                binding.txtHumidity.text = "${
                    String.format(
                        Locale(language),
                        "%d",
                        weather.current.humidity.toInt()
                    )
                } %"
                binding.txtCloud.text ="${
                    String.format(
                        Locale(language),
                        "%d",
                        weather.current.clouds.toInt()
                    )
                } %"
                binding.txtViolet.text = String.format(Locale(language),"%f" ,weather.current.uvi)
                binding.txtVisibility.text =
                    "${
                        String.format(
                            Locale(language),
                            "%d",
                            weather.current.visibility.toInt()
                        )
                    } ${getString(R.string.m)}"
            }
        }
        return binding.root
    }

    private fun getInit() {
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                SharedPrefs.getInstance(requireContext()),
                requireContext()
            )
        )
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        gpsViewModelFactory = GpsViewModelFactory(requireContext())
        gpsViewModel =
            ViewModelProvider(this, gpsViewModelFactory)[GpsViewModel::class.java]

        binding.rvDays.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        daysAdapter = DaysAdapter(ArrayList(), requireContext())
        binding.rvDays.adapter = daysAdapter

        binding.rvHours.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hoursAdapter = HoursAdapter(ArrayList(), requireContext())
        binding.rvHours.adapter = hoursAdapter
    }


    override fun onStart() {
        checkPermissions()
        super.onStart()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show()
//                        gpsViewModel.getLastLocation()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
//                    finish()
                }
                return
            }
        }
    }

//    override fun onStart() {
//
//        if (!checkPermission()) {
//            Log.i("TAG", "onResume: ")
//            requestPermission()
//
//        }
//
//        super.onStart()
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }
//
//    private fun checkPermission(): Boolean {
//        //check the location permissions and return true or false.
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.i("TAG", "checkPermission: true")
//            return true
//        }
//        Log.i("TAG", "checkPermission: false")
//        return false
//    }
//
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ),
//            PERMISSION_ID_GPS
//        )
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_ID_GPS) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Log.i("TAG", "onRequestPermissionsResult: noPremission")
//            }
//
//        }
//    }

    companion object {
        fun convertUTCToLocalDate(time: Long, format: String, lang: String): String {
            val timeD = Date(time * 1000)
            val sdf = SimpleDateFormat(format, Locale(lang))
            return sdf.format(timeD)
        }

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                            return true
                        }
                    }
                }
            }
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}