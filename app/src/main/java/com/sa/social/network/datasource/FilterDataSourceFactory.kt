package com.sa.social.network.datasource

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.google.firebase.firestore.Query
import com.sa.social.network.model.Posts

class FilterDataSourceFactory(context : Context)  : DataSource.Factory<Query, Posts>() {

    var context: Context


    init {
        this.context=context

    }

    private val TAG : String = this.javaClass.getSimpleName()
    private val filterdPostLiveDataSource : MutableLiveData<PageKeyedDataSource<Query, Posts>> = MutableLiveData()

    var description : String = ""
    fun setSearchValue(description: String)
    {
        this.description=description
    }
    fun getPostLiveDataSource(): MutableLiveData<PageKeyedDataSource<Query, Posts>> {
        return filterdPostLiveDataSource
    }

    override fun create(): DataSource<Query, Posts> {

        val DataSource = FilterPostDataSource(context, description)
        filterdPostLiveDataSource.postValue(DataSource)
        return DataSource

    }
}
