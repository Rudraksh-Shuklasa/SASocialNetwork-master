package com.sa.social.network.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.autofill.AutofillValue


public class SharedPrefrenceUtils{



    companion object {
        private var mSharedPref: SharedPreferences? = null
         var IsLogin : String = "IsLogin"
         var IsOpenFirstTime : String = "IsOpenFirstTime"
         var CurrentUser : String="CurrentUser"
         var AuthnticationType : String ="AuthnticationType"

         public fun getSharefPrefrenceManager(context : Context): SharedPreferences?
         {
             if(mSharedPref == null)
                 mSharedPref=context.getSharedPreferences(context.packageName,Activity.MODE_PRIVATE)
             return mSharedPref
         }

     }

}

