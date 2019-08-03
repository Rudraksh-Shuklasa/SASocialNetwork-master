package com.sa.social.network.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.sa.social.network.utils.SharedPrefrenceUtils
import com.sa.social.network.R
import com.sa.social.network.base.BaseActivity


class SplachScreen : AppCompatActivity() {

    private var delayHandler : Handler?= null
    private val delay : Long = 5000



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)

        var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(this)


        //if user not come to screen first time than redirect to splash screen
        delayHandler= Handler()

        delayHandler!!.postDelayed(runnable,delay)

    }

    internal  val runnable : Runnable= Runnable {
        if(!isFinishing)
        {
            var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(this)
            if(sharedPrefManager!!.getBoolean(SharedPrefrenceUtils.Companion.IsLogin,false))
            {
                var goToLogin=Intent(this, MainActivity::class.java)
                startActivity(goToLogin)
                finish()
            }
            else{
                var goToLogin=Intent(this, LoginActivity::class.java)
                startActivity(goToLogin)
                finish()
            }


        }
    }

    override fun onDestroy() {

        if(delayHandler!=null)
            delayHandler!!.removeCallbacks(runnable)

        super.onDestroy()
    }





}
