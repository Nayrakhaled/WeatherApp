package com.example.weatherapp.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.AlertModel
import com.example.weatherapp.model.WeatherAPI

class AlertAdapter(private var listAlert: List<AlertModel>, var context: Context, var listener: onClickListener) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_alerts, parent, false)
        return ViewHolder(view)
    }

    fun setAlertList(listAlert: List<AlertModel>) {
        this.listAlert = listAlert

    }

    override fun onBindViewHolder(holder: AlertAdapter.ViewHolder, position: Int) {
        val alert: AlertModel = listAlert!![position]
        holder.txtDateFrom.text = alert.dateFrom
        holder.txtTimeFrom.text = alert.timeFrom
        holder.txtDateTo.text = alert.dateTo
        holder.txtTimeTo.text = alert.timeTo

        holder.btnDelete.setOnClickListener{
            listener.onClick(alert)
        }

    }


    override fun getItemCount(): Int = listAlert.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtDateFrom: TextView = view.findViewById(R.id.txt_alert_date_from)
        var txtTimeFrom: TextView = view.findViewById(R.id.txt_alert_time_from)
        var txtDateTo: TextView = view.findViewById(R.id.txt_alert_date_to)
        var txtTimeTo: TextView = view.findViewById(R.id.txt_alert_time_to)
        var btnDelete : ImageView = view.findViewById(R.id.img_delete_alert)
    }
}