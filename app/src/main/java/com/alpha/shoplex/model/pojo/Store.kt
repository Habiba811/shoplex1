package com.alpha.shoplex.model.pojo

import java.util.*

data class Store(
    var storeID : String = UUID.randomUUID().toString(),
    var name : String = "",
    var email : String = "",
    var image : String = "",
    var locations : ArrayList<Location> = arrayListOf(),
    var addresses : ArrayList<String> = arrayListOf(),
    var phone : String = ""
)
