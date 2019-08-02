package com.sa.social.network.services

import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import com.facebook.FacebookSdk.getApplicationContext
import androidx.core.content.ContextCompat.getSystemService
import android.net.ConnectivityManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.sa.social.network.base.BaseApplication


class CheckConnetionService : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context))
        }
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}