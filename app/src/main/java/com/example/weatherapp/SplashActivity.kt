package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.databinding.ActivitySplushBinding
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import com.example.weatherapp.setting.view_model.SettingViewModel
import com.example.weatherapp.setting.view_model.SettingViewModelFactory
import com.example.weatherapp.mpa.view.MapsActivity
import java.util.*


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplushBinding
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var settingViewModelFactory: SettingViewModelFactory
    private lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplushBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingViewModelFactory = SettingViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(this),
                SharedPrefs.getInstance(this),
                this
            )
        )
        settingViewModel =
            ViewModelProvider(this, settingViewModelFactory)[SettingViewModel::class.java]

        var lang = Locale.getDefault().language
        val lag = when{
             lang.equals("ar") -> 1
            else -> 0
        }
        settingViewModel.getSetting()
        settingViewModel.setting.observe(this){
            if(it.getInt("Language", -1) == -1 ){
                settingViewModel.saveSetting("Language", lag)
                settingViewModel.saveSetting("Temp", 0)
            }else if(it.getInt("Language", -1) == 1){
                lang = "ar"
            }else{
                lang = "en"
            }
            val config = resources.configuration
            config.setLocale(Locale(lang))
            config.setLayoutDirection(Locale(lang))
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        settingViewModel.getSetting()
        settingViewModel.setting.observe(this) {
            if (it.getInt("Location", -1) == -1) showDialog()
            else startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_setting, null)
        val okBtn = view.findViewById<Button>(R.id.ok_btn)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_loc)
        builder.setView(view)
        okBtn.setOnClickListener {
            val selectedOption: Int = radioGroup!!.checkedRadioButtonId
            radioButton = view.findViewById(selectedOption)
            Log.i("TAG", "showDialog: $selectedOption")
            settingViewModel.saveSetting("Location", selectedOption)
            if (radioButton.text == "Map")
                startActivity(
                    Intent(applicationContext, MapsActivity::class.java).putExtra(
                        "init",
                        "home"
                    )
                )
            else startActivity(Intent(applicationContext, HomeActivity::class.java))
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}


