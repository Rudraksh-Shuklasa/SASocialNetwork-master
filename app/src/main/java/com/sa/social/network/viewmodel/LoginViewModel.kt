package com.sa.social.network.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.sa.social.network.Repositories.AuthnticationRepositorie

class LoginViewModel(application: Application) : AndroidViewModel(application){
    var repos= AuthnticationRepositorie(application.applicationContext)
    private var getCurrentUserLiveData: MutableLiveData<Boolean> = repos.getcurrentUserLiveData()

    fun checkUSerStatus(): FirebaseUser? {
        return repos.checkUserIsLogin()
    }

    fun loginUser(email:String,password:String,activity: Activity) {
        return repos.loginUser(email,password,activity)
    }

    fun getCurrentUser(): MutableLiveData<Boolean> {
        return getCurrentUserLiveData
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount,activity: Activity)
    {
          repos.firebaseAuthWithGoogle(account,activity)
    }

    fun handleFacebookAccessToken(token: AccessToken, activity: Activity){
        repos.handleFacebookAccessToken(token,activity)
    }
}