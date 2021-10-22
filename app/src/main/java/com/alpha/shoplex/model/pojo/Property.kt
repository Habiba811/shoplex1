package com.alpha.shoplex.model.pojo

import androidx.room.Ignore
import com.google.firebase.firestore.Exclude

class Property {
    var propertyID : Int = 0
    var name : String = ""
    var values: ArrayList<String> = arrayListOf()

    @Exclude @get:Exclude @set:Exclude
    @Ignore
    var selectedProperty: String? = null
}