package com.sa.social.network.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.google.firebase.firestore.Query
import com.sa.social.network.model.Notification


class NotificationDataSourceFectory() : DataSource.Factory<Query, Notification>() {

    private val TAG : String = this.javaClass.getSimpleName()
    private val notificationLiveDataSource : MutableLiveData<PageKeyedDataSource<Query, Notification>> = MutableLiveData()

    fun getNotificationLiveDataSource(): MutableLiveData<PageKeyedDataSource<Query, Notification>> {
        return notificationLiveDataSource
    }

    override fun create(): DataSource<Query, Notification> {

        val DataSource = NotificationDataSource()
        notificationLiveDataSource.postValue(DataSource)
        return DataSource

    }


}

