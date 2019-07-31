package com.sa.social.network.model

import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sa.social.network.utils.SharedPrefrenceUtils
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.*
import org.w3c.dom.Comment


class FeedDataRepositore(context : Context) {
    private var storage : FirebaseStorage
    private var storageRef : StorageReference
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var fireDbInstance : FirebaseFirestore
    private var isImagesloaded= MutableLiveData<Boolean>()
    private var postFeedsLiveData = MutableLiveData<ArrayList<Posts>>()
    private var commentLiveData = MutableLiveData<ArrayList<Comments>>()
    private var isCommentAdded = MutableLiveData<Boolean>()
    private var TAG =this.javaClass.simpleName



    init
    {
        storage=FirebaseStorage.getInstance("gs://sasocialmedia-e200c.appspot.com")
        storageRef=storage.reference
        fireDbInstance = FirebaseFirestore.getInstance()
    }

    //image loding status live data
    fun getImageUploadLiveData():MutableLiveData<Boolean>
    {
        return isImagesloaded
    }

    //post live data
    fun getPostFeedLiveData():MutableLiveData<ArrayList<Posts>>
    {
        getPostFeedData()
        return postFeedsLiveData
    }

    //comment live data
    fun getCommentLiveData():MutableLiveData<ArrayList<Comments>>
    {
        return commentLiveData
    }

    //get poostfeed
    fun getPostFeedData():ArrayList<Posts>
    {
        var posts = ArrayList<Posts>()
        val questionsRef = fireDbInstance.collection("Posts")
        questionsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(task.result!!.size() <1)
                {
                    isImagesloaded.postValue(true)
                }
                for (document in task.result!!) {


                    var post=Posts(document["postId"].toString(),
                        document["userId"].toString(),
                        document["photosUrl"].toString(),
                        document["timestamp"] as Long,
                        document["descripition"].toString(),
                        document["likes"] as Long,
                        document["userName"].toString(),
                        document["comments"] as Long,
                        false)

                    fetchPosts(post)
                    posts.add(post)
                }

            }
        }
        return posts
    }

    fun getIsCommentAddedLiveData():MutableLiveData<Boolean>
    {
        return isCommentAdded
    }


    //update post like status
    fun updatePost(post : Posts,isLikeIncrease : Boolean)
    {
        val timestamp = (System.currentTimeMillis() / 1000)
        var post=post
        var like=PostLike(sharedPrefManager?.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString(),timestamp)
        val postLikes = fireDbInstance.collection("Posts").document(post.postId)
        postLikes.update("likes",post.likes).addOnSuccessListener {
            Log.d(TAG,"Likes Updated")
        }


        if(isLikeIncrease)
        {
            fireDbInstance.collection("Posts").document(post.postId).collection("Likes")
                .document(sharedPrefManager?.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString())
                .set(like)
                .addOnSuccessListener {
                    Log.d(TAG,"Updated")
                }
                .addOnFailureListener {
                    Log.d(TAG,it.message)
                }
        }

        else
        {
            fireDbInstance.collection("Posts").document(post.postId).collection("Likes")
                .document(sharedPrefManager?.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG,"Updated")
                }
                .addOnFailureListener {
                    Log.d(TAG,it.message)
                }
        }
    }

    //upload post photo
    fun uploadImage(image: Bitmap,comment : String)
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
                addPostToUserfeed(task.result.toString(),comment)
            } else {
                Log.d(TAG,task.toString())
            }
        }

    }


    //after photo uploaded upload post in Post collection
    private fun addPostToUserfeed(uri:String,quate:String)
    {

        val timestamp = (System.currentTimeMillis() / 1000)
        var postId= UUID.randomUUID().toString() + timestamp.toString()
        var post=Posts(postId,
                       sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString(),
                       uri,
                       timestamp,
                      quate,
                 0,
                       sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserName, "user").toString(),
                       0,
             false)
        fireDbInstance.collection("Posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                isImagesloaded.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }

    }


    //get post with like status
    fun fetchPosts(post:Posts)
    {
        var posts = ArrayList<Posts>()
        var userLikeThePoost=false

        fireDbInstance.collection("Posts").document(post.postId).collection("Likes")
                .whereEqualTo("userId",sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user"))
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        if(task.result!!.documents.size >0)
                        {
                            userLikeThePoost=true
                        }
                         post.isLikedPost=userLikeThePoost
                         posts.add(post)
                        isImagesloaded.postValue(true)
                    }
                }
        postFeedsLiveData.postValue(posts)
    }


    fun addComment(postId : String,commentText : String)
    {
        val timestamp = (System.currentTimeMillis() / 1000)
        var commentId= UUID.randomUUID().toString() + timestamp.toString()
        var comment=Comments(commentId,
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString(),
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserName, "user").toString(),
            timestamp,
            commentText)
            fireDbInstance.collection("Posts").document(postId).collection("Comments").document(commentId)
            .set(comment)
            .addOnSuccessListener {
                isCommentAdded.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG, it.message)
            }
        getComments(postId)
    }


    fun getComments(postId: String):ArrayList<Comments>
    {
        var comments = ArrayList<Comments>()
        val questionsRef = fireDbInstance.collection("Posts").document(postId!!).collection("Comments")
        questionsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(task.result!!.size() <1)
                {
                    isImagesloaded.postValue(true)
                }
                for (document in task.result!!) {


                    var comment=Comments(document["commentId"].toString(),
                        document["userId"].toString(),
                        document["userName"].toString(),
                        document["timestamp"] as Long,
                        document["commentText"].toString())


                    comments.add(comment)
                }

            }
            isCommentAdded.postValue(true)
            commentLiveData.postValue(comments)
        }
        return comments
    }
}





