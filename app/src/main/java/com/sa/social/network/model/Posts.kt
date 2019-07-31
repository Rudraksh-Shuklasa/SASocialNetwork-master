package com.sa.social.network.model


data class Posts(
    var postId: String,
    var userId: String,
    var photosUrl: String,
    var timestamp: Long,
    var descripition: String,
    var likes: Long = 0,
    var userName: String,
    var comments : Long =  0,
    var isLikedPost:Boolean)
