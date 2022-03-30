package com.example.weatherapp.home.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.EXTENDED_IMG
import com.example.weatherapp.ICON_URL
import com.example.weatherapp.R
import com.example.weatherapp.home.view.HomeFragment
import com.example.weatherapp.model.Hourly

class HoursAdapter(private var listHours: List<Hourly>, var context: Context):
    RecyclerView.Adapter<HoursAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_hourly, parent, false)
        return ViewHolder(view)
    }

    fun setHoursList(hourlyList: List<Hourly>) {
        this.listHours = hourlyList
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val hourly: Hourly = listHours!![position]
        holder.textTime.text = HomeFragment.convertUTCToLocalDate(hourly.dt, "hh")
        holder.textTemp.text = "${hourly.temp}"
        Glide.with(context).load(ICON_URL + hourly.weather[0].icon + EXTENDED_IMG).into(holder.imgHourly)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgHourly: ImageView = view.findViewById(R.id.img_hourly)
        val textTime: TextView = view.findViewById(R.id.txt_hourly_time)
        val textTemp: TextView = view.findViewById(R.id.txt_hourly_temp)
    }

    override fun getItemCount(): Int = listHours.size
}
