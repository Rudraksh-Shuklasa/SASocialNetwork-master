package com.sa.social.network.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.google.firebase.firestore.Query
import com.sa.social.network.datasource.ChatMessageDataSource
import com.sa.social.network.datasource.ChatMessageDataSourceFectory
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.model.ChatUser

import com.sa.social.network.model.Notification
import com.sa.social.network.repositories.ChatMessageRepository
import com.sa.social.network.repositories.NotificationRepositories
import com.sa.social.network.utils.SharedPrefrenceUtils

class ChatScreenViewHolder(application: Application):  AndroidViewModel(application) {
    lateinit var chatMessagePagedList: LiveData<PagedList<ChatMessage>>
    lateinit var chatMessageLiveDataSource: LiveData<PageKeyedDataSource<Query, ChatMessage>>
    lateinit var chatMessageDataSourceFactory: ChatMessageDataSourceFectory
    lateinit var isChatMessageLoding : MutableLiveData<Boolean>
    var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(application.applicationContext)
    var recevierUserId:String =""

    private var repos= ChatMessageRepository(application.applicationContext)



    fun setView(recevierId:String)
    {
        var currentUserId:String = sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString()
        chatMessageDataSourceFactory = ChatMessageDataSourceFectory(currentUserId,recevierId)

        chatMessageLiveDataSource = chatMessageDataSourceFactory.getChatMessageLiveDataSource()
        val config = PagedList.Config.Builder()
            .setPageSize(5)
            .setEnablePlaceholders(true)
            .build()

        chatMessagePagedList = LivePagedListBuilder(chatMessageDataSourceFactory, config).build()

        isChatMessageLoding= ChatMessageDataSource.getIsChatMessageLodeing()

    }


    fun refreshChat() {
        chatMessageDataSourceFactory.getChatMessageLiveDataSource().getValue()?.invalidate();
    }

    fun isChatMessageLoaded():MutableLiveData<Boolean>{
        return  isChatMessageLoding
    }

    fun sendMessage(chatMessage: ChatMessage,recevierId:String)
    {
        repos.sendMessage(chatMessage,recevierId)
    }
    fun makeUnreadMessageCountZero(chatUser: ChatUser)
    {
        repos.makeUnreadMessageCountZero(chatUser)

    }


    fun getMessageData(recevierId: String):ArrayList<ChatMessage>{
        return repos.getChatHistory(recevierId)
    }

    fun getMessageLiveData():MutableLiveData<ArrayList<ChatMessage>>{
        return repos.chatMessages
    }

    fun getIsMessageIsLoding():MutableLiveData<Boolean>{
        return repos.isChatIsLoding
    }


}

