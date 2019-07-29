package com.sa.social.network.model

data class ProfileData(
    var userName: String,
    var numberOfPosts: Int,
    var numberOfFollowers: Int,
    var numberOfFollowing: Int,
    var posts: ArrayList<Posts>?
)