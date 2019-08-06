package com.sa.social.network.repositories

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sa.social.network.utils.SharedPrefrenceUtils
import java.io.ByteArrayOutputStream
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.model.Posts
import com.sa.social.network.model.User
import kotlin.collections.ArrayList

///this repo containt all function regardin profile data fetch and all

class ProfileDataRepositorie (context: Context){
    private var storage : FirebaseStorage
    private var storageRef : StorageReference
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var fireDbInstance : FirebaseFirestore
    private var TAG =this.javaClass.simpleName
    private var profileData= MutableLiveData<ArrayList<Posts>>()
    private var editor= sharedPrefManager!!.edit()
    private var userData= MutableLiveData<User>()


    init {
        storage=FirebaseStorage.getInstance("gs://sasocialmedia-e200c.appspot.com")
        storageRef=storage.reference
        fireDbInstance = FirebaseFirestore.getInstance()
    }

    //User object live data
    fun getProfilePostLiveData(): MutableLiveData<ArrayList<Posts>> {
        return profileData
    }


    // user object live data
    fun getUserLiveData():MutableLiveData<User>{
        return userData
    }


    //fetch profile post
    fun getProfilePostsData():ArrayList<Posts>{
        var posts=ArrayList<Posts>()
        val questionsRef = fireDbInstance.collection("Posts")
        questionsRef.whereEqualTo("userId", sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user")).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    var post= Posts(
                        document["postId"].toString(),
                        document["userId"].toString(),
                        document["photosUrl"].toString(),
                        document["timestamp"] as Long,
                        document["descripition"].toString(),
                        document["likes"] as Long,
                        document["userName"].toString(),
                        document["comments"] as Long,
                        false
                    )
                    posts.add(post)
                }
                profileData.postValue(posts)

            }
        }


        return posts

    }



    //upload profile picture
    fun uploadProfilePhoto(image: Bitmap, user: User)
    {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val mountainsRef = storageRef.child(sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user")+"_"+timestamp)

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val data = baos.toByteArray()
        var metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        var uploadTask = mountainsRef.putBytes(data,metadata)

        uploadTask.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
        }

        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation mountainsRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.profilePhotoUrl=task.result.toString()
                updateUser(user)
            } else {
                Log.d(TAG,task.toString())
            }
        }

    }

     //get current profile
    fun getProfile(): User
    {
        var user = User()
        val questionsRef = fireDbInstance.collection("User").whereEqualTo("userId", sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user"))
        val addOnCompleteListener = questionsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, task.result!!.toString())
                for (document in task.result!!) {
                    user = User(
                        document["email"].toString(),
                        document["userName"].toString(),
                        document["creationTimestamp"] as Long,
                        document["userId"].toString(),
                        document["profilePhotoUrl"].toString()
                    )
                    userData.postValue(user)
                }
            }
        }
        return user
    }



    //update user profile
     fun updateUser(user: User)
    {
        var curretUserInfor=user
        fireDbInstance.collection("User")
            .document(curretUserInfor!!.userId)
            .set(curretUserInfor)
            .addOnSuccessListener {
                Log.d(TAG,"User Profile Updated")
                editor.putString(SharedPrefrenceUtils.CurrentUserName,curretUserInfor.userName)
                editor.commit()
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }

        fireDbInstance.collection("User")
        getProfile()
    }

}