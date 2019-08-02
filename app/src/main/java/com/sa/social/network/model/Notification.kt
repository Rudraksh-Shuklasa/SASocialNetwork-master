package com.sa.social.network.model

import java.sql.Timestamp

data class Notification(
    var notificationId : String,
    var title : String,
    var body  : String,
    var timestamp: Long
)