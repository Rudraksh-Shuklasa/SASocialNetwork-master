package com.sa.social.network.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sa.social.network.model.Notification

class NotificationDataSource: PageKeyedDataSource<Query, Notification>()
{

    private val TAG : String = this.javaClass.getSimpleName()
    private var fireDbInstance : FirebaseFirestore
    private var nextSearchFor  : Query
    companion object {
        private var isNotificationLodeing= MutableLiveData<Boolean>()
        fun getIsNotificationLodeing(): MutableLiveData<Boolean>
        {
            return isNotificationLodeing
        }

    }




    init
    {
        fireDbInstance = FirebaseFirestore.getInstance()
        nextSearchFor=fireDbInstance.collection("Notifications")
            .orderBy("timestamp")
            .limit(5)
    }


    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Query>, callback: PageKeyedDataSource.LoadInitialCallback<Query, Notification>)
    {
        isNotificationLodeing.postValue(false)
        var notificationList = ArrayList<Notification>()
        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastNotification = docSnapshot!![docSnapshot.size-1]

                    Log.d(TAG,task.result?.size().toString())

                    for (document in docSnapshot){
                        val notification = Notification(
                            document["notificationId"].toString(),
                            document["title"].toString(),
                            document["body"].toString(),
                            document["timestamp"] as Long
                        )


                        notificationList.add(notification!!)
                        Log.d(TAG,"Notification  :"+notification.notificationId)
                    }




                    nextSearchFor = fireDbInstance.collection("Notifications")
                        .orderBy("timestamp")
                        .startAfter(notificationList)
                        .limit(5)

                    isNotificationLodeing.postValue(true)
                    callback.onResult(notificationList ,null , nextSearchFor)
                }
            }
    }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Query>, callback: PageKeyedDataSource.LoadCallback<Query, Notification>) {

        var notificationList = ArrayList<Notification>()
        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastNotification = docSnapshot!![docSnapshot.size-1]


                    for (document in docSnapshot){
                        val notification = Notification(
                            document["notificationId"].toString(),
                            document["title"].toString(),
                            document["Body"].toString(),
                            document["timestamp"] as Long
                        )

                        notificationList.add(notification!!)
                        Log.d(TAG,"Notification  :"+notification.notificationId)
                    }



                    nextSearchFor = fireDbInstance.collection("Notifications")
                        .orderBy("timestamp")
                        .startAfter(lastNotification)
                        .limit(5)

                    callback.onResult(notificationList ,nextSearchFor)
                }
            }

    }
    override fun loadBefore(params: LoadParams<Query>, callback: LoadCallback<Query, Notification>) {
        Log.d(TAG,"Load Before")
    }



}