package com.alpha.shoplex.model.pojo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Favourite")
data class ProductFavourite(
    @Exclude
    @get:Exclude
    @Ignore
    val product: Product = Product(),
    @Exclude
    @set:Exclude
    @get:Exclude
    @PrimaryKey
    var id: String = ""
) : Product(), Parcelable {
    init {
        this.id = product.productID
        this.productID = product.productID
        this.storeID = product.storeID
        this.storeName = product.storeName
        this.name = product.name
        this.description = product.description
        this.price = product.price
        this.newPrice = product.newPrice
        this.discount = product.discount
        this.category = product.category
        this.subCategory = product.subCategory
        this.rate = product.rate
        this.premium = product.premium
        this.properties = product.properties
        this.date = product.date
        this.images = product.images
    }
}