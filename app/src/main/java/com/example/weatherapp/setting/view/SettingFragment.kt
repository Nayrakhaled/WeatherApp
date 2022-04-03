package com.example.weatherapp.setting.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.*
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import com.example.weatherapp.setting.view_model.SettingViewModel
import com.example.weatherapp.setting.view_model.SettingViewModelFactory

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    lateinit var settingViewModel: SettingViewModel
    private lateinit var settingViewModelFactory: SettingViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingViewModelFactory = SettingViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                SharedPrefs.getInstance(requireContext()),
                requireContext()
            )
        )
        settingViewModel =
            ViewModelProvider(this, settingViewModelFactory)[SettingViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.radioGroupLoc.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view?.findViewById(checkedId)!!
            settingViewModel.saveSetting("Location", radio.text as String)
        }
        binding.radioGroupLag.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view?.findViewById(checkedId)!!
            settingViewModel.saveSetting("Language", radio.text as String)
        }
        binding.radioGroupTemp.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view?.findViewById(checkedId)!!
            val temp = when {
                radio.text.equals("Fahrenheit") -> IMPERIAL
                radio.text.equals("Celsius") -> METRIX
                else -> DEFAULT
            }
            settingViewModel.saveSetting("Temp", temp)
            settingViewModel.saveSetting("Speed", temp)
        }
        binding.radioGroupSpeed.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view?.findViewById(checkedId)!!
            val speed = when {
                radio.text.equals("Mile/Hour") -> IMPERIAL
                else -> DEFAULT
            }
            settingViewModel.saveSetting("Speed", speed)
            settingViewModel.saveSetting("Temp", speed)
        }
        binding.radioGroupNotification.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = view?.findViewById(checkedId)!!
            settingViewModel.saveSetting("Notification", radio.text as String)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}