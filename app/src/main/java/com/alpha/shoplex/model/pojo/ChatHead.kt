package com.alpha.shoplex.model.pojo

import com.google.firebase.Timestamp
import java.util.*

data class ChatHead(
    var productID: String = "",
    var storeId: String = "",
    val chatId: String = "",
    var productName: String = "",
    var price: Float = 0.0F,
    var productImageURL: String? = "",
    val userID: String = "",
    val storeName: String = "",
    var numOfMessage: Int = 0,
    val date: Date = Timestamp.now().toDate(),
    var isStoreOnline: Boolean = false,
    val storePhone: String = "",
    val storeImage: String = ""
)
