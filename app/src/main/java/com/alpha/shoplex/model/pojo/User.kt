package com.alpha.shoplex.model.pojo

import com.google.firebase.firestore.ServerTimestamp
import com.alpha.shoplex.model.enumurations.AuthType
import java.util.*

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var location: Location = Location(0.0, 0.0),
    var address: String = "",
    var phone: String = "",
    var image: String = "",
    val authType: AuthType = AuthType.Email,
    @ServerTimestamp var date: Date? = null
)


