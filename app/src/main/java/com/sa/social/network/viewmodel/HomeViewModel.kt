package com.sa.social.network.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.model.FeedDataRepositore
import com.sa.social.network.model.Posts
import com.sa.social.network.model.ProfileDataRepositorie

class HomeViewModel(application: Application) : AndroidViewModel(application)
{
    private var repos= FeedDataRepositore(application.applicationContext)
    private var getIsImageUploadLiveData: MutableLiveData<Boolean> = repos.getImageUploadLiveData()
    private var getPostFeedLiveData: MutableLiveData<ArrayList<Posts>> = repos.getPostFeedLiveData()


    fun uploadImage(image: Bitmap,comment : String){
        repos.uploadImage(image,comment)
    }

    fun getImageUploadLiveData():MutableLiveData<Boolean> {
        return getIsImageUploadLiveData
    }

    fun getPostFeedLiveData():MutableLiveData<ArrayList<Posts>>
    {
        return getPostFeedLiveData
    }

    fun getPostFeed():ArrayList<Posts>
    {
        return repos.getPostFeedData()
    }

    fun updatePost(post:Posts,isLikeIncrease : Boolean)
    {
        repos.updatePost(post,isLikeIncrease)
    }


}