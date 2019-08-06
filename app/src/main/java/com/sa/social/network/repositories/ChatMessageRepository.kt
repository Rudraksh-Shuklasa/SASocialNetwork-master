package com.sa.social.network.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.sa.social.network.model.ChatMessage
import com.sa.social.network.model.ChatUser
import com.sa.social.network.utils.SharedPrefrenceUtils
import java.util.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlin.collections.ArrayList


class ChatMessageRepository(context : Context){
    private var fireDbInstance : FirebaseFirestore
    private val TAG : String = this.javaClass.getSimpleName()
    private var sharedPrefManager= SharedPrefrenceUtils.Companion.getSharefPrefrenceManager(context)
    var currentUserId=sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId, "user").toString()
    var isChatIsLoding=MutableLiveData<Boolean>()
    var chatMessages = MutableLiveData<ArrayList<ChatMessage>>()


    init {
        fireDbInstance = FirebaseFirestore.getInstance()

    }

    fun getChatLiveData():MutableLiveData<ArrayList<ChatMessage>>{
        return chatMessages
    }

    fun getChatLodingStatus(): MutableLiveData<Boolean>{
        return isChatIsLoding
    }

    fun sendMessage(chatMessage : ChatMessage,reciverId : String){
        val timestamp = (System.currentTimeMillis() / 1000)
        var messageId= UUID.randomUUID().toString() + timestamp.toString()
        chatMessage.messageId=messageId
        chatMessage.timestamp=timestamp

        fireDbInstance.collection("User").document( currentUserId)
            .collection("Friends").document(reciverId).collection("Messages").document(chatMessage.messageId)
            .set(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Message Added")
                addToReceverDocument(chatMessage,reciverId)
            }
            .addOnFailureListener {
                Log.d(TAG,it.message)
            }
    }

    fun addToReceverDocument(chatMessage:ChatMessage,reciverId: String)
    {


        fireDbInstance.collection("User").document(reciverId).collection("Friends").
            document(currentUserId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    fireDbInstance.collection("User").document(reciverId).collection("Friends").
                        document(currentUserId).collection("Messages")
                        .document(chatMessage.messageId)
                        .set(chatMessage)
                        .addOnSuccessListener {
                            //add message and update unread message list
                            val currentUser =
                                fireDbInstance.collection("User").document(reciverId).collection("Friends").
                                    document(currentUserId)

                            fireDbInstance.runTransaction { transaction ->
                                val snapshot = transaction.get(currentUser)

                                val updateUnreadMessage = snapshot.getLong("numberOfUnreadMessage")!! + 1
                                transaction.update(currentUser, "numberOfUnreadMessage", updateUnreadMessage)

                            }.addOnFailureListener {
                                    e -> Log.w(TAG, "numberOfUnreadMessage Increse failure.", e)
                            }



                        }.addOnFailureListener {

                        }
                }
                else {
                    val questionsRef = fireDbInstance.collection("User").whereEqualTo("userId", sharedPrefManager!!.getString(SharedPrefrenceUtils.CurrentUserId,"user"))
                    questionsRef.get().addOnCompleteListener { task ->
                        val document = task.result

                        for (document in task.result!!) {
                            var chatUser = ChatUser(
                                document!!["userId"].toString(),
                                document!!["userName"].toString(),
                                document!!["profilePhotoUrl"].toString(),
                                1
                            )
                            fireDbInstance.collection("User").document(reciverId).collection("Friends").document(document!!["userId"].toString())
                                .set(chatUser)
                                .addOnSuccessListener {
                                    fireDbInstance.collection("User").document(reciverId).collection("Friends").
                                        document(chatUser.userId).collection("Messages").document(chatMessage.messageId)
                                        .set(chatMessage).addOnSuccessListener {
                                            Log.d(TAG,"Message Added")
                                        }
                                        .addOnFailureListener {
                                            Log.d(TAG,"Message Not Added")
                                        }

                                }
                                .addOnFailureListener {
                                    Log.d(TAG,it.message)
                                }
                        }


                    }


                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
            }
        }

    }

    fun makeUnreadMessageCountZero(chatUser: ChatUser)
    {
        chatUser.numberOfUnreadMessage=0
        fireDbInstance.collection("User").document(currentUserId)
            .collection("Friends").document(chatUser.userId)
            .set(chatUser)
            .addOnSuccessListener {
                Log.d(TAG,"Unread Count change")
            }
            .addOnFailureListener {
                Log.d(TAG,"Unread Count not change")
            }

    }


    fun getChatHistory(reciverId:String):ArrayList<ChatMessage>
    {
        var chatMessage=ArrayList<ChatMessage>()
        var doc=fireDbInstance.collection("User").document(currentUserId)
                                 .collection("Friends").document(reciverId).collection("Messages")
                                 .orderBy("timestamp", Query.Direction.DESCENDING)
        doc.addSnapshotListener{ values , e ->
            if(e != null){
                Log.d(TAG,"Somthing Wrong Happen")
                return@addSnapshotListener
            }
            var chatMessageList=ArrayList<ChatMessage>()
            for (document in values!!) {

                val message = ChatMessage(
                    document["messageId"].toString(),
                    document["userId"].toString(),
                    document["userName"].toString(),
                    document["message"].toString(),
                    document["timestamp"] as Long
                )


                chatMessageList.add(message)

            }
            chatMessages.postValue(chatMessageList)
            chatMessage=chatMessageList

        }
        isChatIsLoding.postValue(true)
        return chatMessage
    }



}