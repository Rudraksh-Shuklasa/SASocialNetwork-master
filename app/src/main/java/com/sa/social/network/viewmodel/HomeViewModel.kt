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
import com.sa.social.network.datasource.FilterDataSourceFactory
import com.sa.social.network.datasource.PostDataSource
import com.sa.social.network.datasource.PostsDataSourceFactory
import com.sa.social.network.repositories.*
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
    var filterdPostPagedList: LiveData<PagedList<Posts>>
    private var applicationContext =application.applicationContext
    lateinit var filterPostDataSource  : FilterDataSourceFactory


    init {
        postDataSourceFactory = PostsDataSourceFactory(application.applicationContext)
        filterPostDataSource  =
                FilterDataSourceFactory(applicationContext.applicationContext)

        postLiveDataSource = postDataSourceFactory.getPostLiveDataSource()
        val config = PagedList.Config.Builder()
            .setPageSize(5)
            .setEnablePlaceholders(true)
            .build()

        postPagedList = LivePagedListBuilder(postDataSourceFactory, config).build()
        filterdPostPagedList=LivePagedListBuilder(filterPostDataSource, config).build()
        isFeedIsLoding= PostDataSource.getIsFeedLoding()


    }

    fun RefreshPosts() {
        postDataSourceFactory.getPostLiveDataSource().getValue()?.invalidate();
    }


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
        filterPostDataSource.setSearchValue(description)
        postLiveDataSource = filterPostDataSource.getPostLiveDataSource()
        val config = PagedList.Config.Builder()
            .setPageSize(5)
            .setEnablePlaceholders(true)
            .build()

        filterdPostPagedList = LivePagedListBuilder(filterPostDataSource, config).build()
    }


}