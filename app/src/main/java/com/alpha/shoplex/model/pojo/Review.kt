package com.alpha.shoplex.model.pojo

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Review(
    val productID: String = "",
    val customerName: String = "",
    val image: String? = null,
    val comment: String = "",
    val rate: Float = 0.0F,
    @ServerTimestamp val date: Date = Timestamp.now().toDate()
)