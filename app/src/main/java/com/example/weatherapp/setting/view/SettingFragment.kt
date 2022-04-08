package com.example.weatherapp.setting.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.*
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.gps.view_model.GpsViewModel
import com.example.weatherapp.gps.view_model.GpsViewModelFactory
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.mpa.view.MapsActivity
import com.example.weatherapp.remote.WeatherClient
import com.example.weatherapp.setting.view_model.SettingViewModel
import com.example.weatherapp.setting.view_model.SettingViewModelFactory
import java.util.*

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var gpsViewModel: GpsViewModel
    private lateinit var settingViewModelFactory: SettingViewModelFactory
    private lateinit var gpsViewModelFactory: GpsViewModelFactory

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
        gpsViewModelFactory = GpsViewModelFactory(requireContext())
        settingViewModel =
            ViewModelProvider(this, settingViewModelFactory)[SettingViewModel::class.java]
        gpsViewModel =
            ViewModelProvider(this, gpsViewModelFactory)[GpsViewModel::class.java]


        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        settingViewModel.getSetting()
        settingViewModel.setting.observe(viewLifecycleOwner) {
            when {
                it.getInt("Location", -1) == 1 -> binding.radioButtonMap.isChecked = true
                else -> binding.radioButtonGps.isChecked = true
            }

            when {
                it.getInt("Language", -1) == 1 -> binding.radioButtonArabic.isChecked = true
                else -> binding.radioButtonEng.isChecked = true
            }

            when {
                it.getInt("Temp", -1) == 2 -> binding.radioButtonFahrenheit.isChecked = true
                it.getInt("Temp", -1) == 1 -> binding.radioButtonCelsius.isChecked = true
                else -> binding.radioButtonKelvin.isChecked = true
            }

            when {
                it.getInt("Speed", -1) == 1 -> binding.radioButtonMile.isChecked = true
                else -> binding.radioButtonMeter.isChecked = true
            }

        }

        binding.radioGroupLoc.setOnCheckedChangeListener { _, checkedId ->
            val radio: View = binding.radioGroupLoc.findViewById(checkedId)
            Log.i("TAG", "onCreateView:Radio${binding.radioGroupLoc.indexOfChild(radio)} ")
            settingViewModel.saveSetting("Location", binding.radioGroupLoc.indexOfChild(radio))
        }
        binding.radioGroupLag.setOnCheckedChangeListener { _, checkedId ->
            val radio: View = binding.radioGroupLag.findViewById(checkedId)
            Log.i("TAG", "onCreateView:Radio${binding.radioGroupLag.indexOfChild(radio)} ")
            settingViewModel.saveSetting("Language", binding.radioGroupLag.indexOfChild(radio))

        }
        binding.radioGroupTemp.setOnCheckedChangeListener { _, checkedId ->
            val radio: View = binding.radioGroupTemp.findViewById(checkedId)
            settingViewModel.saveSetting("Temp", binding.radioGroupTemp.indexOfChild(radio))
            settingViewModel.saveSetting("Speed", binding.radioGroupSpeed.indexOfChild(radio))
        }
        binding.radioGroupSpeed.setOnCheckedChangeListener { _, checkedId ->
            val radio: View = binding.radioGroupSpeed.findViewById(checkedId)
            settingViewModel.saveSetting("Speed", binding.radioGroupSpeed.indexOfChild(radio))
            settingViewModel.saveSetting("Temp", binding.radioGroupTemp.indexOfChild(radio))
        }
        binding.radioGroupNotification.setOnCheckedChangeListener { _, checkedId ->
            val radio: View = binding.radioGroupNotification.findViewById(checkedId)
            settingViewModel.saveSetting("Notification", binding.radioGroupNotification.indexOfChild(radio))
        }

        binding.settingBtnOk.setOnClickListener {
            settingViewModel.setting.observe(viewLifecycleOwner) { prefs->
                when {
                    prefs.getInt("Location", -1) == 1 ->
                        startActivity(Intent(requireContext(), MapsActivity::class.java))
                    else -> startActivity(Intent(requireContext(), HomeActivity::class.java))
                }

                Log.i("TAG", "onCreateView: language rrr${prefs.getInt("Language", -1)}")
                when {
                    prefs.getInt("Language", -1) == 1 -> convertLanguage("ar")
                    else -> convertLanguage("en")
                }

            }
        }
        return binding.root
    }

    private fun convertLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            requireContext().createConfigurationContext(config)
        }
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}