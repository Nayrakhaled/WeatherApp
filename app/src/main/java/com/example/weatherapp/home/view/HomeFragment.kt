package com.example.weatherapp.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.API_KEY_WEATHER
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.home.view.adapter.DaysAdapter
import com.example.weatherapp.home.view.adapter.HoursAdapter
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.Hourly
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private val binding get() = _binding!!
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(WeatherClient.getInstance(), requireContext())
        )
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel.getCurrentWeather(37.422F, -122.084F, "minutely", API_KEY_WEATHER)

        binding.rvDays.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        daysAdapter = DaysAdapter(ArrayList<Daily>(), requireContext())
        binding.rvDays.adapter = daysAdapter

        binding.rvHours.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hoursAdapter = HoursAdapter(ArrayList<Hourly>(), requireContext())
        binding.rvHours.adapter = hoursAdapter

        homeViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            binding.txtCity.text = weather.timezone
            binding.txtDate.text = convertUTCToLocalDate(weather.current.dt, "EEE, dd MMM")
            binding.txtDesc.text = weather.current.weather[0].description
            binding.txtDegree.text = weather.current.temp.toString()

            binding.txtPressure.text = weather.current.pressure.toString()
            binding.txtHumidity.text = weather.current.humidity.toString()
            binding.txtWind.text = weather.current.wind_speed.toString()
            binding.txtCloud.text = weather.current.clouds.toString()
            binding.txtViolet.text = weather.current.uvi.toString()
            binding.txtVisibility.text = weather.current.visibility.toString()

            daysAdapter.setDaysList(weather.daily)
            daysAdapter.notifyDataSetChanged()

            hoursAdapter.setHoursList(weather.hourly)
            hoursAdapter.notifyDataSetChanged()
        }


        return binding.root
    }


    companion object {
        fun convertUTCToLocalDate(time: Long, format: String): String {
            val timeD = Date(time * 1000)
            val sdf = SimpleDateFormat(format)
            return sdf.format(timeD)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}