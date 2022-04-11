package com.example.weatherapp.favourite.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.home.view.HomeFragment
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.WeatherAPI

class FavAdapter(
    private var listFav: List<WeatherAPI>,
    var listener: OnClickListener,
    var context: Context
) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    private lateinit var city: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_fav, parent, false)
        return ViewHolder(view)
    }

    fun setFavList(listFav: List<WeatherAPI>, city: String) {
        this.listFav = listFav
        this.city = city
    }

    override fun onBindViewHolder(holder: FavAdapter.ViewHolder, position: Int) {
        val weather: WeatherAPI = listFav!![position]
        holder.txtName.text = city
        holder.itemView.setOnClickListener {
            listener.onClick(weather)
        }
        holder.imgDelete.setOnClickListener {
            Log.i("TAG", "onBindViewHolder: ${weather.timezone}")
            listener.onClickDelete(weather)
        }
    }

    override fun getItemCount(): Int = listFav.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtName: TextView = view.findViewById(R.id.txt_fav_name)
        var imgDelete: ImageView = view.findViewById(R.id.img_delete)
    }

}
