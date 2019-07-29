package com.sa.social.network.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.model.DataRepositorie
import com.sa.social.network.model.User

class EditProfileViewModel(application : Application) : AndroidViewModel(application)
{
    private var repos= DataRepositorie(application.applicationContext)
    private var usetDate: MutableLiveData<User> = repos.getUserLiveData()

    fun getProfile() : User{
        return repos.getProfile()
    }

    fun getUsetDate():MutableLiveData<User> {
        return usetDate
    }

}