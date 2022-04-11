package com.example.weatherapp.fav_detail.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.*
import com.example.weatherapp.databinding.ActivityFavouriteDetailsBinding
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.fav_detail.view_model.FavDetailViewModel
import com.example.weatherapp.fav_detail.view_model.FavDetailViewModelFactory
import com.example.weatherapp.gps.view_model.GpsViewModel
import com.example.weatherapp.gps.view_model.GpsViewModelFactory
import com.example.weatherapp.home.view.HomeFragment
import com.example.weatherapp.home.view.adapter.DaysAdapter
import com.example.weatherapp.home.view.adapter.HoursAdapter
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import com.google.android.gms.maps.model.LatLng
import java.util.*

class FavouriteDetail: AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteDetailsBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var favDetailViewModel: FavDetailViewModel
    private lateinit var favDetailViewModelFactory: FavDetailViewModelFactory
    private lateinit var gpsViewModel: GpsViewModel
    private lateinit var gpsViewModelFactory: GpsViewModelFactory
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var language: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInit()
        Log.i("TAG", "onCreate: ")
        val city = intent.getStringExtra("FAV").toString()
        Log.i("TAG", "onCreate: $city")
        favDetailViewModel.getFavDetailWeather(city)
        homeViewModel.getDataFromShared()
        homeViewModel.data.observe(this) {
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
        }
        favDetailViewModel.weatherFav.observe(this){weather ->
            binding.txtCity.text = String.format(Locale(language), "%s", gpsViewModel.getCity(LatLng(weather.lat, weather.lon), language))
            binding.txtDate.text =
                HomeFragment.convertUTCToLocalDate(weather.current.dt, "EEE, dd MMM", language)
            binding.txtDesc.text = weather.current.weather[0].description
            Glide.with(this)
                .load(ICON_URL + weather.current.weather[0].icon + EXTENDED_IMG)
                .override(150, 70)
                .into(binding.imgCurrent)

            homeViewModel.data.observe(this) {
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

    }

    private fun getInit() {

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(this),
                SharedPrefs.getInstance(this),
                this
            )
        )
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        gpsViewModelFactory = GpsViewModelFactory(this)
        gpsViewModel =
            ViewModelProvider(this, gpsViewModelFactory)[GpsViewModel::class.java]

        favDetailViewModelFactory = FavDetailViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(this),
                SharedPrefs.getInstance(this),
                this
            )
        )
        favDetailViewModel =
            ViewModelProvider(this, favDetailViewModelFactory)[FavDetailViewModel::class.java]


        binding.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        daysAdapter = DaysAdapter(ArrayList(), this)
        binding.rvDays.adapter = daysAdapter

        binding.rvHours.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hoursAdapter = HoursAdapter(ArrayList(), this)
        binding.rvHours.adapter = hoursAdapter
    }
}