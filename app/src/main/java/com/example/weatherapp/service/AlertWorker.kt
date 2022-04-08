package com.example.weatherapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.CHANNEL_ID
import com.example.weatherapp.R


class AlertWorker( var context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {


    override fun doWork(): Result {

        showNotification()
        return Result.success()
    }

    private fun showNotification() {
       // , intent: PendingIntent?
//        val notifyIntent = Intent(applicationContext, RfillDialogActivity::class.java)
        // Set the Activity to start in a new, empty task
//        notifyIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
//                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        // Create the PendingIntent
//        val notifyPendingIntent = PendingIntent.getActivity(
//            applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        Log.i("TAG", "showNotification: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "channel"
            val desc = "desc"
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
            .setContentTitle("Refill Reminder")
            .setContentText("is about to end")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(intent)
            .setSound(alarmSound)
            .setAutoCancel(true).build()
        val managerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        managerCompat.notify(2, notification)
    }

}