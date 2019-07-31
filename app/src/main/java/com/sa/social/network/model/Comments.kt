package com.sa.social.network.model

import java.sql.Timestamp

data class Comments (

    var commentId : String,
    var userId    : String,
    var userName  : String,
    var timestamp : Long,
    var commentText : String
)