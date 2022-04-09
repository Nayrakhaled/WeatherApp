package com.example.weatherapp.alert.view

import android.R.attr.delay
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.R
import com.example.weatherapp.alert.view_model.AlertViewModel
import com.example.weatherapp.alert.view_model.AlertViewModelFactory
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import com.example.weatherapp.service.AlertWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class AlertFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var alertAdapter: AlertAdapter
    private var alertList: List<AlertModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                SharedPrefs.getInstance(requireContext()),
                requireContext()
            )
        )
        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),
                SharedPrefs.getInstance(requireContext()),
                requireContext()
            )
        )
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        binding.imgNoneNotif.isVisible = when {
            alertList.isEmpty() -> true
            else -> false
        }
        binding.rvAlert.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        alertAdapter = AlertAdapter(alertList, requireContext())
        binding.rvAlert.adapter = alertAdapter


        binding.fabAlert.setOnClickListener {
            showAlertDialog()
        }
        return binding.root
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_alert, null)
        val txtDateFrom: TextView = view.findViewById(R.id.txt_date_from_dialog)
        val txtTimeFrom: TextView = view.findViewById(R.id.txt_time_from_dialog)
        val txtDateTo: TextView = view.findViewById(R.id.txt_date_to_dialog)
        val txtTimeTo: TextView = view.findViewById(R.id.txt_time_to_dialog)
        val okBtn: Button = view.findViewById(R.id.save_dialog_btn)

        builder.setView(view)

        txtDateFrom.text = SimpleDateFormat("dd-MM-yyyy").format(Date())
        txtTimeFrom.text = SimpleDateFormat("HH:mm").format(Date())
        txtDateTo.text = SimpleDateFormat("dd-MM-yyyy").format(Date())
        txtTimeTo.text = SimpleDateFormat("HH:mm").format(Date())


        val currentTime = Calendar.getInstance()
        var time = ""

        txtTimeTo.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                currentTime.set(Calendar.HOUR_OF_DAY, hour)
                currentTime.set(Calendar.MINUTE, minute)
                txtTimeTo.text = SimpleDateFormat("HH:mm").format(currentTime.time)
                time = SimpleDateFormat("HH:mm").format(currentTime.time)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener, currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE), true
            ).show()
        }

        txtDateTo.setOnClickListener {
            val dateListener = DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                currentTime.set(Calendar.YEAR, mYear)
                currentTime.set(Calendar.MONTH, mMonth)
                currentTime.set(Calendar.DAY_OF_MONTH, mDay)
                txtDateTo.text = SimpleDateFormat("dd-MM-yyyy").format(currentTime.time)
                getDaysBetweenDates(
                    SimpleDateFormat("dd-MM-yyyy").format(Date()),
                    SimpleDateFormat("dd-MM-yyyy").format(currentTime.time),
                    time
                )
            }
            DatePickerDialog(
                requireContext(), dateListener,
                currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                currentTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        alertList = listOf(
            AlertModel(
                txtDateFrom.text.toString(),
                txtTimeFrom.text.toString(),
                txtDateTo.text.toString(),
                txtTimeTo.text.toString()
            )
        )


        okBtn.setOnClickListener {
            alertAdapter.setAlertList(alertList)
            alertAdapter.notifyDataSetChanged()
            binding.imgNoneNotif.isVisible = false
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun getDaysBetweenDates(startDate: String, endDate: String, time: String) {
        val daysList = ArrayList<String>()
        var date = startDate
        daysList.add(startDate)
        while (date != endDate) {
            daysList.add(date)
            date = incrementCalenderDate(date)
        }
        doAlertManager(daysList.size, time)
    }

    private fun incrementCalenderDate(date: String): String {
        var date = date
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val c = Calendar.getInstance()
        c.time = sdf.parse(date)
        c.add(Calendar.DATE, 1)
        date = sdf.format(c.time)
        return date
    }

    private fun doAlertManager(listDays: Int, time: String) {
        val requests = ArrayList<WorkRequest>()
        homeViewModel.getDataFromShared()
        homeViewModel.data.observe(viewLifecycleOwner) {
            val inputData = Data.Builder().putDouble("lat", it.getString("lat", null)!!.toDouble())
                .putDouble("log", it.getString("log", null)!!.toDouble()).build()

            var delay = calcDelay(time)
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build()

            val request = OneTimeWorkRequest.Builder(AlertWorker::class.java)
                .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
                .setInputData(inputData)
                .setConstraints(constraint)
                .build()
            requests.add(request)
            for (i in 1..listDays) {
                delay = calcDelay(time + (24 * 60))
                val request = OneTimeWorkRequest.Builder(AlertWorker::class.java)
                    .setInitialDelay(delay.toLong(), TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .build()
                requests.add(request)
            }
            WorkManager.getInstance().enqueue(requests)
        }
    }

    private fun calcDelay(date: String): Int {
        val currentDate: String = SimpleDateFormat("HH:mm").format(Date())

        val dateSplit1 = currentDate.split(":").toTypedArray()
        val dateSplit2 = date!!.split(":").toTypedArray()
        val dateH1 = dateSplit1[0]
        val dateM1 = dateSplit1[1]
        val dateH2 = dateSplit2[0]
        val dateM2 = dateSplit2[1]
        val diffH = abs(dateH1.toInt() - dateH2.toInt())
        val diffM = abs(dateM1.toInt() - dateM2.toInt())
        val delay = if (diffH >= 1) diffH * 60 + diffM else diffH + diffM
        Log.i("TAG", " :: $delay")
        return delay
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}