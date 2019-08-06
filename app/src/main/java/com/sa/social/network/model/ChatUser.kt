package com.sa.social.network.model

import java.io.Serializable


data class ChatUser(var userId : String,var userName : String, var userProfilePicture : String,var numberOfUnreadMessage : Long):
    Serializable