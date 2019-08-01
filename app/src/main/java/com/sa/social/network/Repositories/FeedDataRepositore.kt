package com.sa.social.network.Repositories

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sa.social.network.model.Comments
import com.sa.social.network.model.PostLike
import com.sa.social.network.model.Posts
import com.sa.social.network.utils.SharedPrefrenceUtils
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class FeedDataRepositore(context : Context) {
    private var storage : FirebaseStorage
    private var storageRef : StorageReference
    private val TAG : String = this.javaClass.getSimpleName()
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var fireDbInstance : FirebaseFirestore
    private var isImagesloaded= MutableLiveData<Boolean>()
    private var commentLiveData = MutableLiveData<ArrayList<Comments>>()
    private var isCommentAdded = MutableLiveData<Boolean>()







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

    //comment live data
    fun getCommentLiveData():MutableLiveData<ArrayList<Comments>>
    {
        return commentLiveData
    }


    fun getIsCommentAddedLiveData():MutableLiveData<Boolean>
    {
        return isCommentAdded
    }


    //update post like status
    fun updatePostLikes(postId: String ,isLikeIncrease : Boolean)
    {
        val timestamp = (System.currentTimeMillis() / 1000)
        var like= PostLike(
            sharedPrefManager?.getString(
                SharedPrefrenceUtils.CurrentUserId,
                "user"
            ).toString(), timestamp
        )


            if(isLikeIncrease)
            {

                val curretPost =   fireDbInstance.collection("Posts").document(postId)

                fireDbInstance.runTransaction { transaction ->
                    val snapshot = transaction.get(curretPost)

                    val updatedLikes = snapshot.getLong("likes")!! + 1
                    transaction.update(curretPost, "likes", updatedLikes)

                }.addOnSuccessListener {
                    fireDbInstance.collection("Posts").document(postId).collection("Likes")
                        .document(sharedPrefManager?.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString())
                        .set(like)
                        .addOnSuccessListener {
                            Log.d(TAG,"Updated")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.message)
                        }
                    Log.d(TAG, "Likes Count Update success!")
                }.addOnFailureListener {
                        e -> Log.w(TAG, "Likes Count Increse failure.", e)
                }


            }

            else
            {
                val curretPost =   fireDbInstance.collection("Posts").document(postId)

                fireDbInstance.runTransaction { transaction ->
                    val snapshot = transaction.get(curretPost)

                    val updatedLikes = snapshot.getLong("likes")!! - 1
                    transaction.update(curretPost, "likes", updatedLikes)

                }.addOnSuccessListener {
                    fireDbInstance.collection("Posts").document(postId).collection("Likes")
                        .document(sharedPrefManager?.getString(SharedPrefrenceUtils.CurrentUserId,"user").toString())
                        .delete()
                        .addOnSuccessListener {
                            Log.d(TAG,"Updated")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.message)
                        }
                    Log.d(TAG, "Likes Update success!")
                }.addOnFailureListener {
                        e -> Log.w(TAG, "Likes Increse failure.", e)
                }

            }
        }

    fun updatePostComment(postId: String)
    {
        val curretPost =   fireDbInstance.collection("Posts").document(postId)

        fireDbInstance.runTransaction { transaction ->
            val snapshot = transaction.get(curretPost)

            val updatedComments = snapshot.getLong("comments")!! + 1
            transaction.update(curretPost, "comments", updatedComments)

        }.addOnSuccessListener {

            Log.d(TAG, "Comment Count Update success!")
        }.addOnFailureListener {
                e -> Log.w(TAG, "Comment Count Update failure.", e)
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
        var post= Posts(
            postId,
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString(),
            uri,
            timestamp,
            quate,
            0,
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserName, "user").toString(),
            0,
            false
        )
        fireDbInstance.collection("Posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                isImagesloaded.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }

    }



    //add comment
    fun addComment(postId : String,commentText : String)
    {
        val timestamp = (System.currentTimeMillis() / 1000)
        var commentId= UUID.randomUUID().toString() + timestamp.toString()
        var comment= Comments(
            commentId,
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString(),
            sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserName, "user").toString(),
            timestamp,
            commentText
        )
            fireDbInstance.collection("Posts").document(postId).collection("Comments").document(commentId)
            .set(comment)
            .addOnSuccessListener {
                updatePostComment(postId)
                isCommentAdded.postValue(true)
            }
            .addOnFailureListener {
                Log.d(TAG, it.message)
            }
        getComments(postId)
    }


    // get comment
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


                    var comment= Comments(
                        document["commentId"].toString(),
                        document["userId"].toString(),
                        document["userName"].toString(),
                        document["timestamp"] as Long,
                        document["commentText"].toString()
                    )


                    comments.add(comment)
                }

            }
            isCommentAdded.postValue(true)
            commentLiveData.postValue(comments)
        }
        return comments
    }


    //search Post
    fun searchPostByDesctiption(desription : String)
    {
        val questionsRef = fireDbInstance.collection("Posts")
        questionsRef.whereEqualTo("descripition",desription).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(task.result!!.size() <1)
                {
                    isImagesloaded.postValue(true)
                }
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

                    //fetchPosts(post)
                }

            }
        }
    }


}





