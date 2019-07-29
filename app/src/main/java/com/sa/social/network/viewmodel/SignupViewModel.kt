package com.sa.social.network.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.sa.social.network.model.AuthnticationRepositorie


class SignupViewModel(application: Application) : AndroidViewModel(application)
{
    var repos=AuthnticationRepositorie(application.applicationContext)
    private var getCurrentUserLiveData: MutableLiveData<Boolean> = repos.getcurrentUserLiveData()



    fun checkUSerStatus(): FirebaseUser? {
        return repos.checkUserIsLogin()
    }

    fun signinUser(email:String,password:String,activity: Activity) {
        return repos.signinUser(email,password,activity)
    }



    fun getCurrentUser(): MutableLiveData<Boolean> {
        return getCurrentUserLiveData
    }
}