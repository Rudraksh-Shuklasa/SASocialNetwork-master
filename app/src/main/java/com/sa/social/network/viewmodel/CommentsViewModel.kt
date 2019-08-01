package com.sa.social.network.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.model.Comments
import com.sa.social.network.Repositories.FeedDataRepositore


class CommentsViewModel(application : Application) : AndroidViewModel(application)
{
    private var repos= FeedDataRepositore(application.applicationContext)
    private var commentsData: MutableLiveData<ArrayList<Comments>> = repos.getCommentLiveData()
    private var isCommentAdded : MutableLiveData<Boolean> = repos.getIsCommentAddedLiveData()

    fun getCommentData():MutableLiveData<ArrayList<Comments>> {
        return commentsData
    }

    fun getIsCommentAdded() : MutableLiveData<Boolean>{
        return isCommentAdded
    }

    fun addComment(postId:String,commentText : String){
        repos.addComment(postId,commentText)
    }

    fun getComments(postId: String):ArrayList<Comments>{
        return repos.getComments(postId)
    }




}