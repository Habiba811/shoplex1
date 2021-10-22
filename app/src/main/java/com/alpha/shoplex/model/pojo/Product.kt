package com.alpha.shoplex.model.pojo

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Ignore
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

open class Product : Parcelable {
    var productID: String = UUID.randomUUID().toString()
    var storeID: String = ""
    var storeName: String = ""
    var storeLocation: Location = Location()
    var name: String = ""
    var description: String = ""
    var price: Float = 10F
    var newPrice: Float = 10F
    var discount: Float = 0F
    var category: String = ""
    var subCategory: String = ""
    @Ignore
    var deleted: Boolean = false

    @Nullable
    var rate: Float? = null

    @Nullable
    var premium: Premium? = null
    var properties: ArrayList<Property> = arrayListOf()

    @ServerTimestamp
    var date: Date? = null
    var quantity: Int = 1
    var sold: Int = 0

    var images: ArrayList<String?> = arrayListOf()

    @Ignore
    @Exclude
    @set:Exclude
    @get:Exclude
    var imagesListURI: ArrayList<Uri> = arrayListOf()

    @Ignore
    @Exclude
    @set:Exclude
    @get:Exclude
    var imageSlideList: ArrayList<SlideModel> = arrayListOf()

    @Ignore
    @Exclude
    @set:Exclude
    @get:Exclude
    var isFavourite = false

    @Ignore
    @Exclude
    @set:Exclude
    @get:Exclude
    var isCart = false

    constructor()

    constructor(
        name: String,
        price: Float,
        category: String,
        productImageUrl: String
    ) {
        this.name = name
        this.price = price
        this.newPrice = price
        this.category = category
        this.images.add(productImageUrl)
    }

    constructor(
        name: String,
        newPrice: Float,
        oldPrice: Float,
        rate: Float,
        productImageUrl: String
    ) {
        this.name = name
        this.newPrice = newPrice
        this.price = oldPrice
        this.rate = rate
        this.images[0] = productImageUrl
    }

    constructor(parcel: Parcel) : this() {
        productID = parcel.readString().toString()
        name = parcel.readString().toString()
        description = parcel.readString().toString()
        price = parcel.readFloat()
        newPrice = parcel.readFloat()
        discount = parcel.readFloat()
        category = parcel.readString().toString()
        subCategory = parcel.readString().toString()
        premium = parcel.readParcelable(Premium::class.java.classLoader)
        imagesListURI = parcel.readArrayList(Uri::class.java.classLoader) as ArrayList<Uri>
        quantity = parcel.readInt()
        rate = parcel.readFloat()
    }

    @Exclude
    fun getImageSlides(): ArrayList<SlideModel> {
        this.imageSlideList.clear()
        for (image in imagesListURI) {
            imageSlideList.add(SlideModel(image.toString()))
        }
        return imageSlideList
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productID)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeFloat(price)
        parcel.writeFloat(newPrice)
        parcel.writeFloat(discount)
        parcel.writeString(category)
        parcel.writeString(subCategory)
        parcel.writeParcelable(premium, 0)
        parcel.writeArray(imagesListURI.toArray())
        parcel.writeInt(quantity)
        rate?.let { parcel.writeFloat(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}