package com.sa.social.network.model

import android.util.Log

data class User(var email : String,var userName : String,var creationTimestamp : Long,var userId : String,var profilePhotoUrl : String) {
    constructor() : this("demo","demo",0,"demo","demo")
}