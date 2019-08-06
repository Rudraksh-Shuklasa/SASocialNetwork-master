package com.sa.social.network.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.sa.social.network.model.Comments
import com.sa.social.network.model.Notification
import com.sa.social.network.model.Posts
import com.sa.social.network.utils.SharedPrefrenceUtils
import java.util.*
import kotlin.collections.ArrayList

class NotificationRepositories {
    private var fireDbInstance : FirebaseFirestore
    private val TAG : String = this.javaClass.getSimpleName()
    private var notificationLiveData= MutableLiveData<ArrayList<Notification>>()

    init {
        fireDbInstance = FirebaseFirestore.getInstance()
    }


    fun getNotificationLiveData():MutableLiveData<ArrayList<Notification>>
    {
        return notificationLiveData
    }

    fun deleteNotification(notification:Notification){
        fireDbInstance
            .collection("Notification")
            .document(notification.notificationId)
            .delete().addOnSuccessListener{
                Log.d(TAG,"Notification Deleted")
            }
            .addOnFailureListener {
                Log.d(TAG,"Notification Not Deleted")
            }
    }



    fun uploadNotificationData(notification: Notification){


        fireDbInstance.collection("Notifications").document(notification.notificationId)
            .set(notification)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }
    }
}
