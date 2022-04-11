package com.example.weatherapp.favourite.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.fav_detail.view.FavouriteDetail
import com.example.weatherapp.favourite.view_model.FavViewModelFactory
import com.example.weatherapp.favourite.view_model.FavouriteViewModel
import com.example.weatherapp.gps.view_model.GpsViewModel
import com.example.weatherapp.gps.view_model.GpsViewModelFactory
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.mpa.view.MapsActivity
import com.example.weatherapp.remote.WeatherClient
import com.google.android.gms.maps.model.LatLng


class FavouriteFragment : Fragment(), OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favViewModel: FavouriteViewModel
    private lateinit var favViewModelFactory: FavViewModelFactory
    private lateinit var gpsViewModelFactory: GpsViewModelFactory
    private lateinit var gpsViewModel: GpsViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favAdapter: FavAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        getInit()

        favViewModel.getAllFavWeather()
        favViewModel.favWeather.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.imgNoneFav.isVisible = true
            } else {
                Log.i("TAG", "onCreateView: ${list.size}")
                homeViewModel.getDataFromShared()
                homeViewModel.data.observe(viewLifecycleOwner) {
                    val lang = when {
                        it.getInt("Language", -1) == 1 -> "ar"
                        else -> "en"
                    }
                    favAdapter.setFavList(list, lang)

                    favAdapter.notifyDataSetChanged()
                    binding.imgNoneFav.isVisible = false
                }
            }
        }
        binding.fab.setOnClickListener {
            startActivity(
                Intent(requireContext(), MapsActivity::class.java).putExtra(
                    "favourite",
                    "fav"
                )
            )
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

        favViewModelFactory = FavViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                SharedPrefs.getInstance(requireContext()),
                requireContext()
            )
        )
        favViewModel =
            ViewModelProvider(this, favViewModelFactory)[FavouriteViewModel::class.java]

        gpsViewModelFactory = GpsViewModelFactory(requireContext())
        gpsViewModel =
            ViewModelProvider(this, gpsViewModelFactory)[GpsViewModel::class.java]

        binding.rvFav.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favAdapter = FavAdapter(ArrayList(), this, requireContext(), gpsViewModel)
        binding.rvFav.adapter = favAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(weather: WeatherAPI) {
        startActivity(
            Intent(requireContext(), FavouriteDetail::class.java).putExtra(
                "FAV",
                weather.timezone
            )
        )
    }

    override fun onClickDelete(weather: WeatherAPI) {
        favViewModel.deleteFavWeather(weather)
        favViewModel.getAllFavWeather()
        favViewModel.favWeather.observe(viewLifecycleOwner) {
            Log.i("TAG", "onClickDelete: ${it.size}")
            if (it.isEmpty()) {
                binding.imgNoneFav.isVisible = true
                favAdapter.setFavList(it, "")
                favAdapter.notifyDataSetChanged()
            } else {
                Log.i("TAG", "onCreateView:size ${it.size}")
                favAdapter.setFavList(it, "")
                favAdapter.notifyDataSetChanged()
                binding.imgNoneFav.isVisible = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        favViewModel.getAllFavWeather()
        favViewModel.favWeather.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.imgNoneFav.isVisible = true
            } else {
                Log.i("TAG", "onStart: ${list.size}")
                homeViewModel.getDataFromShared()
                homeViewModel.data.observe(viewLifecycleOwner) {
                    val lang = when {
                        it.getInt("Language", -1) == 1 -> "ar"
                        else -> "en"
                    }
                    favAdapter.setFavList(list, lang)

                    favAdapter.notifyDataSetChanged()
                    binding.imgNoneFav.isVisible = false
                }
            }
        }
    }
}