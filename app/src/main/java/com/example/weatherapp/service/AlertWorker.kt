package com.example.weatherapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.roomdemomvvm.db.ConcreteLocalSource
import com.example.weatherapp.CHANNEL_ID
import com.example.weatherapp.HomeActivity
import com.example.weatherapp.R
import com.example.weatherapp.home.view.HomeFragment
import com.example.weatherapp.local.sharedPrefs.SharedPrefs
import com.example.weatherapp.model.WeatherAPI
import com.example.weatherapp.model.repository.Repository
import com.example.weatherapp.remote.WeatherClient
import java.text.SimpleDateFormat
import java.util.*


class AlertWorker(var context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val repo =
        Repository.getInstance(
            WeatherClient.getInstance(),
            ConcreteLocalSource(context),
            SharedPrefs.getInstance(context),
            context
        )

    var weather = WeatherAPI()


    override suspend fun doWork(): Result {

        weather = repo.getCurrentWeather(
            inputData.getDouble("lat", 0.0),
            inputData.getDouble("log", 0.0),
            "",
            ""
        )

        if (weather.alerts?.isNullOrEmpty()!!) {
            showNotification("No News", "The weather is good")
        } else {
            val startDate = HomeFragment
                .convertUTCToLocalDate(weather.alerts!![0].start.toLong(), "dd-MM-yyyy", "en")
            val endDate = HomeFragment
                .convertUTCToLocalDate(weather.alerts!![0].end.toLong(), "dd-MM-yyyy", "en")
            val list = getDaysBetweenDates(startDate, endDate)
            val currentDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
            for (i in list.indices) {
                if (list[i] == currentDate) {
                    showNotification(
                        weather.alerts!![0].sender_name,
                        weather.alerts!![0].description
                    )
                } else {
                    showNotification("No News", "The weather is good")
                }
            }
        }
        return Result.success()
    }

    private fun getDaysBetweenDates(startDate: String, endDate: String): List<String> {
        val daysList = ArrayList<String>()
        var date = startDate
        daysList.add(startDate)
        while (date != endDate) {
            daysList.add(date)
            date = incrementCalenderDate(date)
        }
        return daysList
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

    private fun showNotification(title: String, desc: String) {
        val notifyIntent = Intent(applicationContext, HomeActivity::class.java)
        notifyIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // Create the PendingIntent
        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = desc
            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
//        val sound =
//            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/quite_impressed.mp3")
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_cloud_24)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notifyPendingIntent)
            .setSound(alarmSound)
            .setAutoCancel(true).build()
        val managerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        managerCompat.notify(2, notification)
    }
}