package com.sa.social.network.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.google.firebase.firestore.Query
import com.sa.social.network.Repositories.FeedDataRepositore
import com.sa.social.network.Repositories.PostDataSource
import com.sa.social.network.Repositories.PostsDataSourceFactory
import com.sa.social.network.model.*

class HomeViewModel(application: Application) : AndroidViewModel(application)
{
    private var repos= FeedDataRepositore(application.applicationContext)
    private var getIsImageUploadLiveData: MutableLiveData<Boolean> = repos.getImageUploadLiveData()
    private var recordsList = mutableListOf<Comments>()
    private var isImagesloaded= MutableLiveData<Boolean>()


    var isFeedIsLoding : MutableLiveData<Boolean>
    var postPagedList: LiveData<PagedList<Posts>>
    var postLiveDataSource: LiveData<PageKeyedDataSource<Query, Posts>>
    var postDataSourceFactory: PostsDataSourceFactory


    init {
        postDataSourceFactory =
                PostsDataSourceFactory(application.applicationContext)
        postLiveDataSource = postDataSourceFactory.getPostLiveDataSource()

        val config = PagedList.Config.Builder()
            .setPageSize(5)
            .setEnablePlaceholders(true)
            .build()

        postPagedList = LivePagedListBuilder(postDataSourceFactory, config).build()
        isImagesloaded.postValue(true)
        isFeedIsLoding= PostDataSource.getIsFeedLoding()

    }

    fun swipeRefresh() {
        postDataSourceFactory.getPostLiveDataSource().getValue()?.invalidate();
    }


    private val _records = MutableLiveData<List<Comments>>()
    val records: LiveData<List<Comments>> = _records

    fun uploadImage(image: Bitmap,comment : String){
        repos.uploadImage(image,comment)
    }

    fun getImageUploadLiveData():MutableLiveData<Boolean> {
        return getIsImageUploadLiveData
    }
    fun isFeedLoded():MutableLiveData<Boolean>{
        return  isFeedIsLoding
    }



    fun updatePostLikes(postId:String,isLikeIncrease : Boolean)
    {
        repos.updatePostLikes(postId,isLikeIncrease)
    }

    fun searchPostByDesctiption(description: String){
        repos.searchPostByDesctiption(description)
    }


}