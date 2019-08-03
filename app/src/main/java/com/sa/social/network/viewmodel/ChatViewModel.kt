package com.sa.social.network.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sa.social.network.base.BaseApplication
import com.sa.social.network.model.ChatUser
import com.sa.social.network.model.Posts
import com.sa.social.network.model.User
import com.sa.social.network.repositories.ChatRepository
import com.sa.social.network.repositories.ProfileDataRepositorie

class ChatViewModel(application: Application):  AndroidViewModel(application){
    var repos= ChatRepository(application.applicationContext)
    private var userLiveData: MutableLiveData<ArrayList<User>> = repos.getUserLiveData()
    private var chatUserLiveData: MutableLiveData<ArrayList<ChatUser>> = repos.getChatUserLiveData()
    private var ChatIsLoaded: MutableLiveData<Boolean> = repos.getIsChatIsLoaded()

    fun getChatUserLiveData():MutableLiveData<ArrayList<ChatUser>>
    {
        return chatUserLiveData
    }
    fun getUserRequestLiveData():MutableLiveData<ArrayList<User>>
    {
        return userLiveData
    }

    fun ChatIsLoaded():MutableLiveData<Boolean>
    {
        return ChatIsLoaded
    }

    fun getUserData():ArrayList<User> {

        return repos.getUserData()
    }

    fun addChatUser(user:User){
        repos.addUserForChat(user)
    }

    fun isUserAdded():MutableLiveData<Boolean>{
       return repos.isUserAdded()
    }

    fun getChatUserData():ArrayList<ChatUser>{
        return repos.getChatUserData()
    }
}