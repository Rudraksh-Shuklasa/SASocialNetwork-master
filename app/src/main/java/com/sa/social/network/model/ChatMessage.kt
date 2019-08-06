package com.sa.social.network.model

data class ChatMessage(
    var messageId : String,
    var userId : String,
    var userName : String,
    var message : String,
    var timestamp : Long)