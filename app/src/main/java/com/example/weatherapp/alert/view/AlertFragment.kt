package com.example.weatherapp.alert.view

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
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.R
import com.example.weatherapp.alert.view_model.AlertViewModel
import com.example.weatherapp.alert.view_model.AlertViewModelFactory
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import java.text.SimpleDateFormat
import java.util.*


class AlertFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
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
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]

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

        txtDateFrom.text = SimpleDateFormat("dd MMM, YYYY").format(Date())
        txtTimeFrom.text = SimpleDateFormat("hh:mm a").format(Date())
        txtDateTo.text = SimpleDateFormat("dd MMM, YYYY").format(Date())
        txtTimeTo.text = SimpleDateFormat("hh:mm a").format(Date())


        val currentTime = Calendar.getInstance()

        txtTimeTo.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, _ ->
                currentTime.set(Calendar.HOUR_OF_DAY, hour)
                txtTimeTo.text = SimpleDateFormat("hh:mm a").format(currentTime.time)
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
                txtDateTo.text = SimpleDateFormat("dd MMM, YYYY").format(currentTime.time)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}