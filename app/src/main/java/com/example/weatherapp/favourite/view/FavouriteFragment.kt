package com.example.weatherapp.favourite.view

import android.content.Intent
import android.content.Intent.getIntent
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
import com.example.weatherapp.favourite.view_model.FavViewModelFactory
import com.example.weatherapp.favourite.view_model.FavouriteViewModel
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.mpa.view.MapsActivity
import com.example.weatherapp.remote.WeatherClient


class FavouriteFragment : Fragment(), OnClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favViewModel: FavouriteViewModel
    private lateinit var favViewModelFactory: FavViewModelFactory
    private lateinit var favAdapter: FavAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

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

        binding.rvFav.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favAdapter = FavAdapter(ArrayList(), this, requireContext())
        binding.rvFav.adapter = favAdapter
        Log.i("TAG", "onCreateView: favvvv")
        favViewModel.getAllFavWeather()
        favViewModel.favWeather.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.imgNoneFav.isVisible = true
            } else {
                Log.i("TAG", "onCreateView:size ${it.size}")
                favAdapter.setFavList(it)
                favAdapter.notifyDataSetChanged()
                binding.imgNoneFav.isVisible = false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(weather: WeatherAPI) {

    }

    override fun onClickDelete(weather: WeatherAPI) {
        favViewModel.deleteFavWeather(weather)
        favViewModel.getAllFavWeather()
        favViewModel.favWeather.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.imgNoneFav.isVisible = true
                favAdapter.setFavList(it)
                favAdapter.notifyDataSetChanged()
            } else {
                Log.i("TAG", "onCreateView:size ${it.size}")
                favAdapter.setFavList(it)
                favAdapter.notifyDataSetChanged()
                binding.imgNoneFav.isVisible = false
            }
        }
    }
}