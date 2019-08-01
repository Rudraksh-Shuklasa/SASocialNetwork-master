package com.sa.social.network.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.Repositories.ProfileDataRepositorie
import com.sa.social.network.model.User

class EditProfileViewModel(application : Application) : AndroidViewModel(application)
{
    private var repos= ProfileDataRepositorie(application.applicationContext)
    private var usetDate: MutableLiveData<User> = repos.getUserLiveData()

    fun getProfile() : User{
        return repos.getProfile()
    }

    fun getUsetDate():MutableLiveData<User> {
        return usetDate
    }

    fun setUserProfile(profile:Bitmap,user: User){
        repos.uploadProfilePhoto(profile,user)
    }

    fun setUserProfile(user: User){
        repos.updateUser(user)
    }



}