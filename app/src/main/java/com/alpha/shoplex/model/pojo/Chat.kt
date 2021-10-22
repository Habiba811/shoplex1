package com.alpha.shoplex.model.pojo

import com.google.firebase.firestore.Exclude

data class Chat(
    var chatID: String = "",
    val userID: String = "",
    val storeID: String = "",
    val userName: String = "",
    val storeName: String = "",
    val storePhone: String = "",
    @JvmField
    val isClientOnline: Boolean = false,
    @JvmField
    val isStoreOnline: Boolean = false,
    val unreadCustomerMessages: Int = 0,
    @Exclude @set:Exclude
    var unreadStoreMessages: Int = 0,
    var productIDs: ArrayList<String> = arrayListOf(String()),
    val storeImage: String = ""
)