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
import com.example.weatherapp.model.Daily

class DaysAdapter(private var listDays: List<Daily>, var context: Context): RecyclerView.Adapter<DaysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_daily, parent, false)
        return ViewHolder(view)
    }

    fun setDaysList(dailyList: List<Daily>) {
        this.listDays = dailyList
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val daily: Daily = listDays!![position]
        holder.textDay.text = HomeFragment.convertUTCToLocalDate(daily.dt, "EEE")
        holder.textDesc.text = daily.weather[0].main
        holder.textTemp.text = "${daily.temp.min } / ${ daily.temp.max}"
        Glide.with(context).load(ICON_URL + daily.weather[0].icon + EXTENDED_IMG).into(holder.imgDays)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDays: ImageView = view.findViewById(R.id.img_days)
        val textDay: TextView = view.findViewById(R.id.txt_days_name)
        val textDesc: TextView = view.findViewById(R.id.txt_days_desc)
        val textTemp: TextView = view.findViewById(R.id.txt_days_temp)
    }

    override fun getItemCount(): Int = listDays.size
}
