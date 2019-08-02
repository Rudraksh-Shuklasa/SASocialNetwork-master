package com.sa.social.network.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.google.firebase.firestore.Query
import com.sa.social.network.model.Posts

class PostsDataSourceFactory(context : Context) : DataSource.Factory<Query, Posts>() {
     var context: Context

    init {
         this.context=context
    }

    private val TAG : String = this.javaClass.getSimpleName()
    private val postLiveDataSource : MutableLiveData<PageKeyedDataSource<Query, Posts>> = MutableLiveData()

    fun getPostLiveDataSource(): MutableLiveData<PageKeyedDataSource<Query, Posts>> {
        return postLiveDataSource
    }

    override fun create(): DataSource<Query, Posts> {

        val DataSource = PostDataSource(context)
        postLiveDataSource.postValue(DataSource)
        return DataSource

    }


}

