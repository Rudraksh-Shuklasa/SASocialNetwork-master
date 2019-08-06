package com.sa.social.network.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.google.firebase.firestore.Query
import com.sa.social.network.model.Notification
import com.sa.social.network.datasource.NotificationDataSource
import com.sa.social.network.datasource.NotificationDataSourceFectory
import com.sa.social.network.repositories.NotificationRepositories

class NotificationViewModel(application: Application) : AndroidViewModel(application)
{

    var isNotificationIsLoding : MutableLiveData<Boolean>
    var notificationPagedList: LiveData<PagedList<Notification>>
    var notificationLiveDataSource: LiveData<PageKeyedDataSource<Query, Notification>>
    var notificationDataSourceFactory: NotificationDataSourceFectory
    private var repos= NotificationRepositories()
    init {
        notificationDataSourceFactory = NotificationDataSourceFectory()


        notificationLiveDataSource = notificationDataSourceFactory.getNotificationLiveDataSource()
        val config = PagedList.Config.Builder()
            .setPageSize(5)
            .setEnablePlaceholders(true)
            .build()

        notificationPagedList = LivePagedListBuilder(notificationDataSourceFactory, config).build()

        isNotificationIsLoding= NotificationDataSource.getIsNotificationLodeing()


    }

    fun deleteNotification(notification: Notification){
        repos.deleteNotification(notification)
    }
    fun refreshNotification() {
        notificationDataSourceFactory.getNotificationLiveDataSource().getValue()?.invalidate();
    }

    fun isNotificationLoded():MutableLiveData<Boolean>{
        return  isNotificationIsLoding
    }
}