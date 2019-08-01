package com.sa.social.network.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.Repositories.ProfileDataRepositorie
import com.sa.social.network.model.*

class ProfileViewModel(application: Application) : AndroidViewModel(application)
{
    var repos= ProfileDataRepositorie(application.applicationContext)
    private var getProfilePostLiveData: MutableLiveData<ArrayList<Posts>> = repos.getProfilePostLiveData()
    private var usetDate: MutableLiveData<User> = repos.getUserLiveData()


    fun getProfilePost(): MutableLiveData<ArrayList<Posts>> {
        return getProfilePostLiveData
    }

    fun getProfileData():ArrayList<Posts>{
        return repos.getProfilePostsData()
    }



    fun getProfile() : User {
        return repos.getProfile()
    }

    fun getUsetDate():MutableLiveData<User> {
        return usetDate
    }

}