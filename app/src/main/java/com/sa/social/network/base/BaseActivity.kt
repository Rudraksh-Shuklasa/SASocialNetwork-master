package com.sa.social.network.base

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sa.social.network.R
import com.sa.social.network.services.CheckConnetionService
import android.content.Intent
import android.provider.Settings
import androidx.localbroadcastmanager.content.LocalBroadcastManager


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), CheckConnetionService.ConnectivityReceiverListener {
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(CheckConnetionService(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }


    private fun showMessage(isConnected: Boolean) {


        if (!isConnected) {

            Toast.makeText(this, "Please Start Internet First", Toast.LENGTH_SHORT).show()

            var intent=Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        CheckConnetionService.connectivityReceiverListener = this
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(CheckConnetionService());

    }
}