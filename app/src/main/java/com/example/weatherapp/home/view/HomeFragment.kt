package com.example.weatherapp.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var gpsViewModel: GpsViewModel
    private lateinit var gpsViewModelFactory: GpsViewModelFactory
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private val binding get() = _binding!!
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter

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
                it.getString("Language", null).equals("Arabic") -> "ar"
                else -> "en"
            }
            gpsViewModel.getLastLocation()
            if (isOnline(requireContext())) {
                Log.i("TAG", "onCreateView: online")
                gpsViewModel.location.observe(viewLifecycleOwner) { loc ->
                    Log.i("TAG", "onCreateView: ${loc.latitude}")
                    homeViewModel.getCurrentWeather(
                        loc.latitude,
                        loc.longitude, lang, it.getString("Temp", null) as String
                    )
                }
            } else {
                homeViewModel.getWeather()
            }
        }

        homeViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            binding.txtCity.text = weather.timezone
            binding.txtDate.text = convertUTCToLocalDate(weather.current.dt, "EEE, dd MMM")
            binding.txtDesc.text = weather.current.weather[0].description

            homeViewModel.data.observe(viewLifecycleOwner) {
                Log.i("TAG", "onCreateView: ${it.getString("Temp", null)}")
                val temp = when {
                    it.getString("Temp", null).equals(IMPERIAL) -> FAHRENHEIT
                    it.getString("Temp", null).equals(METRIX) -> CELSIUS
                    else -> KELVIN
                }
                binding.txtDegree.text = "${weather.current.temp} $temp"

                daysAdapter.setDaysList(weather.daily, temp)
                daysAdapter.notifyDataSetChanged()

                hoursAdapter.setHoursList(weather.hourly, temp)
                hoursAdapter.notifyDataSetChanged()

                val speed = when {
                    it.getString("Speed", null).equals(IMPERIAL) -> MILE_HOUR
                    else -> METER_SEC
                }
                Log.i("TAG", "onCreateView:Speed $speed")
                binding.txtWind.text = "${weather.current.wind_speed} $speed"
            }

            binding.txtPressure.text = "${weather.current.pressure} hpa"
            binding.txtHumidity.text = "${weather.current.humidity} %"
            binding.txtCloud.text = "${weather.current.clouds} %"
            binding.txtViolet.text = weather.current.uvi.toString()
            binding.txtVisibility.text = "${weather.current.visibility} m"
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


    companion object {
        fun convertUTCToLocalDate(time: Long, format: String): String {
            val timeD = Date(time * 1000)
            val sdf = SimpleDateFormat(format)
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