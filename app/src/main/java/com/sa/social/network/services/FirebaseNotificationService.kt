package com.sa.social.network.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sa.social.network.model.Notification
import com.sa.social.network.repositories.NotificationRepositories

import android.app.PendingIntent
import android.content.Intent
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sa.social.network.views.MainActivity
import com.sa.social.network.R


class FirebaseNotificationService : FirebaseMessagingService(){
    var TAG = this.javaClass.simpleName
    private var repos= NotificationRepositories()

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val timestamp = (System.currentTimeMillis() / 1000)

        var notification=Notification(
            message!!.messageId!!,
            message!!.getNotification()!!.title!!,
            message!!.getNotification()!!.body!!,
            timestamp
            )

        Log.d(TAG,"Reciving firebase notification")

        repos.uploadNotificationData(notification)
        sendNotification(message!!.getNotification()!!.body!!)
    }


    private fun sendNotification(msg: String) {
        var mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificatioId = System.currentTimeMillis()

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("Firebase",1)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentIntent = PendingIntent.getActivity(this, (Math.random() * 100).toInt(), intent, 0)

        var currentapiVersion = android.os.Build.VERSION.SDK_INT
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            currentapiVersion = R.drawable.ic_logo_splash
        } else {
            currentapiVersion = R.drawable.ic_logo_splash
        }
        Log.d(TAG,"Reciving firebase notification")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationBuilder = NotificationCompat.Builder(this, "fcm_channel")
                .setSmallIcon(currentapiVersion)
                .setContentTitle(this.resources.getString(R.string.app_name))
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
            mNotificationManager.notify(notificatioId.toInt(), notificationBuilder.build())
        }
        else
        {
            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(currentapiVersion)
                .setContentTitle(this.resources.getString(R.string.app_name))
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
            mNotificationManager.notify(notificatioId.toInt(), notificationBuilder.build())
        }
    }


}