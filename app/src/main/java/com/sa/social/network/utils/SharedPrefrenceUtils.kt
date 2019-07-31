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
         var CurrentUserId : String="CurrentUserId"
         var CurrentUserName : String="CurrentUserName"
         var AuthnticationType : String ="AuthnticationType"

         public fun getSharefPrefrenceManager(context : Context): SharedPreferences?
         {
             if(mSharedPref == null)
                 mSharedPref=context.getSharedPreferences(context.packageName,Activity.MODE_PRIVATE)
             return mSharedPref
         }

        fun deleteSharedPrefrence(context: Context)
        {
            context.getSharedPreferences(context.packageName, 0).edit().clear().commit();
        }

     }

}

