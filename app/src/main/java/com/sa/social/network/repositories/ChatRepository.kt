package com.sa.social.network.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sa.social.network.model.Comments
import com.sa.social.network.utils.SharedPrefrenceUtils
import android.widget.Toast
import com.google.firebase.firestore.*
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.model.ChatUser
import com.sa.social.network.model.User
import com.sa.social.network.views.MainActivity



class ChatRepository(context : Context) {
    private val TAG : String = this.javaClass.getSimpleName()
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var fireDbInstance : FirebaseFirestore
    private var isChatIsloaded= MutableLiveData<Boolean>()
    private var userLiveData = MutableLiveData<ArrayList<User>>()
    private var userAdded = MutableLiveData<Boolean>()
    private var chatUserdataLiveData = MutableLiveData<ArrayList<ChatUser>>()
    private var currentUserId=sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString()

    init{
        fireDbInstance = FirebaseFirestore.getInstance()
    }

    fun getChatUserLiveData():MutableLiveData<ArrayList<ChatUser>>{
        return chatUserdataLiveData
    }

    fun getIsChatIsLoaded():MutableLiveData<Boolean>
    {
        return isChatIsloaded
    }

    fun isUserAdded():MutableLiveData<Boolean>{
        return userAdded
    }

    fun getUserLiveData():MutableLiveData<ArrayList<User>>
    {
        return userLiveData
    }


    fun getUserData(chatUserList: ArrayList<ChatUser>):ArrayList<User>
    {
        val userList = ArrayList<User>()
        fireDbInstance.collection("User")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val userList = ArrayList<User>()
                for (user in value!!) {

                    if(!user["userId"].toString().equals(currentUserId))
                    {
                        var isUserIsChatUser=false
                        for(chatUser  in chatUserList) {
                            if(chatUser.userId.equals(user["userId"].toString()))
                            {
                                isUserIsChatUser=true
                                break
                            }

                        }
                        if(!isUserIsChatUser)
                        {
                            var userObject =User(
                                user["email"].toString(),
                                user["userName"].toString(),
                                user["creationTimestamp"] as Long,
                                user["userId"].toString(),
                                user["profilePhotoUrl"].toString()
                            )
                            userList.add(userObject)
                        }


                    }
                }
                userLiveData.postValue(userList)
                Log.d(TAG, "charUser : "+userList.toString())
            }

        return userList


    }


    fun getChatUserData():ArrayList<ChatUser>
    {
        val userList = ArrayList<ChatUser>()
        fireDbInstance.collection("User").document(currentUserId)
            .collection("Friends")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val userList = ArrayList<ChatUser>()
                for (user in value!!) {

                        var userObject =ChatUser(
                            user["userId"].toString(),
                            user["userName"].toString(),
                            user["userProfilePicture"].toString(),
                            user["numberOfUnreadMessage"] as Long
                        )
                        userList.add(userObject)

                }
                chatUserdataLiveData.postValue(userList)
                Log.d(TAG, "charUser : "+userList.toString())
            }

        return userList
    }

    fun addUserForChat(user:User)
    {
        var chatUser = ChatUser(user.userId,user.userName,user.profilePhotoUrl,0)
        fireDbInstance.collection("User").document( currentUserId)
            .collection("Friends").document(user.userId)
            .set(chatUser)
            .addOnSuccessListener {
                userAdded.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }
    }
    


}