package com.sa.social.network.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.sa.social.network.R
import java.lang.Appendable

class BaseApplication :Application(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()


        val notificationChannel = NotificationChannel("fcm_channel", "Fire Base Notification", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

}