package com.alpha.shoplex.model.pojo

data class NotificationToken(
    val userID: String = "",
    val tokenID: String = "",
    val userType: String = "Client",
    val notification: Boolean = true
)