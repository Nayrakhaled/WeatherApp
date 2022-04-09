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
import java.util.*
import kotlin.math.roundToInt

class DaysAdapter(private var listDays: List<Daily>, var context: Context): RecyclerView.Adapter<DaysAdapter.ViewHolder>() {

    private lateinit var temp: String
    lateinit var lang: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_daily, parent, false)
        return ViewHolder(view)
    }

    fun setDaysList(dailyList: List<Daily>, temp: String, lang: String) {
        this.listDays = dailyList
        this.temp = temp
        this.lang = lang
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val daily: Daily = listDays!![position]
        holder.textDay.text = HomeFragment.convertUTCToLocalDate(daily.dt, "EEE", lang)
        holder.textDesc.text = daily.weather[0].description
        holder.textTemp.text = "${String.format(Locale(lang),"%d", daily.temp.min.roundToInt())} / ${String.format(Locale(lang), "%d",daily.temp.max.roundToInt())}  $temp"
        Glide.with(context).load(ICON_URL + daily.weather[0].icon + EXTENDED_IMG).into(holder.imgDays)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDays: ImageView = view.findViewById(R.id.img_days)
        val textDay: TextView = view.findViewById(R.id.txt_days_name)
        val textDesc: TextView = view.findViewById(R.id.txt_days_desc)
        val textTemp: TextView = view.findViewById(R.id.txt_days_temp)
    }

    override fun getItemCount(): Int = listDays.size-1
}
