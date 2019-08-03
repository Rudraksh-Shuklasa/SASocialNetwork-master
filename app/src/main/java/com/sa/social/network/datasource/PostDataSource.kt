package com.sa.social.network.datasource

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.android.gms.tasks.Task

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sa.social.network.utils.SharedPrefrenceUtils
import kotlinx.coroutines.*
import com.sa.social.network.model.Posts
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PostDataSource(context : Context) : PageKeyedDataSource<Query, Posts>()
{

    private val TAG : String = this.javaClass.getSimpleName()
    private var fireDbInstance : FirebaseFirestore
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    private var nextSearchFor  : Query



    companion object {
        private var isFeedLodeing= MutableLiveData<Boolean>()
        fun getIsFeedLoding():MutableLiveData<Boolean>
        {
            return isFeedLodeing
        }

    }




    init
    {

        fireDbInstance = FirebaseFirestore.getInstance()

        nextSearchFor=fireDbInstance.collection("Posts")
            .orderBy("timestamp")
            .limit(5)
    }


    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Query>, callback: PageKeyedDataSource.LoadInitialCallback<Query, Posts>)
    {
        isFeedLodeing.postValue(false)
        var postList = ArrayList<Posts>()
        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastPost = docSnapshot!![docSnapshot.size-1]

                    Log.d(TAG,task.result?.size().toString())

                        for (document in docSnapshot){
                            val post = Posts(
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

                                GlobalScope.launch (Dispatchers.Unconfined) {
                                    post.isLikedPost = synceOperation(post)
                                }


                            postList.add(post!!)
                            Log.d(TAG,"post  :"+post.isLikedPost)
                    }




                    nextSearchFor = fireDbInstance.collection("Posts")
                        .orderBy("timestamp")
                        .startAfter(lastPost)
                        .limit(5)

                    isFeedLodeing.postValue(true)
                    callback.onResult(postList ,null , nextSearchFor)
                }
            }
    }

    override fun loadAfter(params: LoadParams<Query>, callback: LoadCallback<Query, Posts>) {

        var postList = ArrayList<Posts>()
        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->

                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastPost = docSnapshot!![docSnapshot.size-1]


                        for (document in docSnapshot){
                            val post = Posts(
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
                            GlobalScope.launch (Dispatchers.IO) {
                                        post.isLikedPost = synceOperation(post)
                                    }

                            postList.add(post!!)
                            Log.d(TAG,"post  :"+post.postId)
                        }



                    nextSearchFor = fireDbInstance.collection("Posts")
                        .orderBy("timestamp")
                        .startAfter(lastPost)
                        .limit(5)

                    callback.onResult(postList ,nextSearchFor)
                }
            }

    }





    private suspend fun synceOperation(post: Posts)=suspendCoroutine<Boolean>{
        var userLikeThePoost=false
        var task= fireDbInstance.collection("Posts").document(post.postId).collection("Likes")
            .whereEqualTo("userId",sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user"))
            .get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                if(task.result!!.documents.size >0)
                {
                    userLikeThePoost=true
                }
                it.resume(userLikeThePoost)

            }
        }

    }



    override fun loadBefore(params: PageKeyedDataSource.LoadParams<Query>, callback: PageKeyedDataSource.LoadCallback<Query, Posts>) {
       Log.d(TAG,"before")
        isFeedLodeing.postValue(true)
    }


}

