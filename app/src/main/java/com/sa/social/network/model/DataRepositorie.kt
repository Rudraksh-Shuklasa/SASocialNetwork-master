package com.sa.social.network.model

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
import java.util.*
import kotlin.collections.ArrayList


class DataRepositorie (context: Context){
    private var storage : FirebaseStorage
    private var storageRef : StorageReference
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var fireDbInstance : FirebaseFirestore
    private var TAG =this.javaClass.simpleName
    private var profileData= MutableLiveData<ArrayList<Posts>>()
    private var userData= MutableLiveData<User>()
    private var isImageUpload= MutableLiveData<Boolean>()

    init {
        storage=FirebaseStorage.getInstance("gs://sasocialmedia-e200c.appspot.com")
        storageRef=storage.reference
        fireDbInstance = FirebaseFirestore.getInstance()
    }

    fun getProfileLiveData(): MutableLiveData<ArrayList<Posts>> {
        return profileData
    }

    fun getImageUploadLiveData():MutableLiveData<Boolean> {
        return isImageUpload
    }
    fun getUserLiveData():MutableLiveData<User>{
        return userData
    }

    fun getProfilePostsData():ArrayList<Posts>{
        var posts=ArrayList<Posts>()
        val questionsRef = fireDbInstance.collection("Posts")
        questionsRef.whereEqualTo("userId", sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUser,"user")).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    var post=Posts(document["postId"].toString(),document["userId"].toString(),document["photosUrl"].toString(), document["timestamp"] as Long,document["descripition"].toString(),document["likes"] as Long)
                    posts.add(post)
                }
                profileData.postValue(posts)

            }
        }


        return posts

    }

    fun uploadImage(image: Bitmap,comment : String){

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val mountainsRef = storageRef.child(sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUser,"user")+"_"+timestamp)

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
                addPostToUserfeed(task.result.toString(),comment)
            } else {
                Log.d(TAG,task.toString())
            }
        }

    }

    fun addPostToUserfeed(uri:String,comment:String){

        val timestamp = (System.currentTimeMillis() / 1000)
        var postId=UUID.randomUUID().toString() + timestamp.toString()
        var post=Posts(postId,sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUser,"user"),uri,timestamp,comment,0)
        fireDbInstance.collection("Posts")
            .document()
            .set(post)
            .addOnSuccessListener {
                isImageUpload.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }

    }

    fun getProfile():User
    {
        var user = User()
        val questionsRef = fireDbInstance.collection("User")
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
}