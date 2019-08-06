package com.sa.social.network.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.google.firebase.firestore.Query
import com.sa.social.network.datasource.ChatMessageDataSource
import com.sa.social.network.model.ChatMessage

class ChatMessageDataSourceFectory(currentUserId:String,recevierId:String)  : DataSource.Factory<Query, ChatMessage>() {

    private var currentUserId : String
    private var receiverUserId : String

    init {
        this.currentUserId=currentUserId
        this.receiverUserId=recevierId
    }

    private val TAG : String = this.javaClass.getSimpleName()
    private val chatMessageLiveDataSource : MutableLiveData<PageKeyedDataSource<Query, ChatMessage>> = MutableLiveData()

    fun getChatMessageLiveDataSource(): MutableLiveData<PageKeyedDataSource<Query, ChatMessage>> {
        return chatMessageLiveDataSource
    }

    override fun create(): DataSource<Query,ChatMessage> {

        val DataSource = ChatMessageDataSource(currentUserId,receiverUserId)
        chatMessageLiveDataSource.postValue(DataSource)
        return DataSource

    }
}
