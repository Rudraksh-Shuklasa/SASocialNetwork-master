package com.sa.social.network.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.model.ChatUser

class ChatMessageDataSource(currentUserId:String,receiverUserId:String): PageKeyedDataSource<Query, ChatMessage>()
{

    private val TAG : String = this.javaClass.getSimpleName()
    private var fireDbInstance : FirebaseFirestore
    private var currentUserId : String
    private var receiverUserId : String
    private var nextSearchFor  : Query
    companion object {
        private var isChatMessageLodeing= MutableLiveData<Boolean>()
        fun getIsChatMessageLodeing(): MutableLiveData<Boolean>
        {
            return isChatMessageLodeing
        }

    }


    init
    {
        fireDbInstance = FirebaseFirestore.getInstance()
        this.currentUserId=currentUserId
        this.receiverUserId=receiverUserId
        nextSearchFor=fireDbInstance.collection("User")
            .document(currentUserId).collection("Friends").document(receiverUserId).collection("Messages")
            .orderBy("timestamp",Query.Direction.ASCENDING)
            .limit(15)
    }


    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Query>, callback: PageKeyedDataSource.LoadInitialCallback<Query, ChatMessage>)
    {
        isChatMessageLodeing.postValue(false)
        var messageList = ArrayList<ChatMessage>()
        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastMessage = docSnapshot!![docSnapshot.size-1]

                    Log.d(TAG,task.result?.size().toString())


                    for (document in docSnapshot){
                        val message = ChatMessage(
                            document["messageId"].toString(),
                            document["userId"].toString(),
                            document["userName"].toString(),
                            document["message"].toString(),
                            document["timestamp"] as Long
                        )


                        messageList.add(message!!)
                        Log.d(TAG,"message  :"+message.messageId)
                    }




                    nextSearchFor=fireDbInstance.collection("User")
                        .document(currentUserId).collection("Friends").document(receiverUserId).collection("Messages")
                        .orderBy("timestamp",Query.Direction.ASCENDING)
                        .startAfter(lastMessage)
                        .limit(15)

                    isChatMessageLodeing.postValue(true)
                    callback.onResult(messageList ,null , nextSearchFor)
                }
                else
                {
                    isChatMessageLodeing.postValue(true)
                }
            }
    }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Query>, callback: PageKeyedDataSource.LoadCallback<Query, ChatMessage>) {
        var messageList = ArrayList<ChatMessage>()

        nextSearchFor.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if(task.isSuccessful && !task.result?.isEmpty!!){
                    val docSnapshot = task.result?.documents

                    val lastMessage = docSnapshot!![docSnapshot.size-1]


                    for (document in docSnapshot){
                        val message = ChatMessage(
                            document["messageId"].toString(),
                            document["userId"].toString(),
                            document["userName"].toString(),
                            document["message"].toString(),
                            document["timestamp"] as Long
                        )

                        messageList.add(message!!)
                        Log.d(TAG,"message  :"+message.messageId)
                    }




                    nextSearchFor=fireDbInstance.collection("User")
                        .document(currentUserId).collection("Friends").document(receiverUserId).collection("Messages")
                        .orderBy("timestamp",Query.Direction.ASCENDING)
                        .startAfter(lastMessage)
                        .limit(15)



                    callback.onResult(messageList ,nextSearchFor)
                }
            }

    }



    override fun loadBefore(params: LoadParams<Query>, callback: LoadCallback<Query, ChatMessage>) {
        Log.d(TAG,"Load Before")
    }



}