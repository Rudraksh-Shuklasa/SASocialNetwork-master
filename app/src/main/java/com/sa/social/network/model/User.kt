package com.sa.social.network.model

import android.util.Log
import java.io.Serializable

data class User(var email : String,var userName : String,var creationTimestamp : Long,var userId : String,var profilePhotoUrl : String) : Serializable{
    constructor() : this("demo","demo",0,"demo","demo")
}