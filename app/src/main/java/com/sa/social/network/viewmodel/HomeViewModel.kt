package com.sa.social.network.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.model.DataRepositorie
import com.sa.social.network.model.ProfileData

class HomeViewModel(application: Application) : AndroidViewModel(application)
{
    private var repos= DataRepositorie(application.applicationContext)
    private var getIsImageUploadLiveData: MutableLiveData<Boolean> = repos.getImageUploadLiveData()


    fun uploadImage(image: Bitmap,comment : String){
        repos.uploadImage(image,comment)
    }

    fun getImageUploadLiveData():MutableLiveData<Boolean> {
        return getIsImageUploadLiveData
    }


}