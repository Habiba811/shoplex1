package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable

class Filter(
    val lowPrice: Int? = null,
    val highPrice: Int? = null,
    val subCategory: ArrayList<String>? = null,
    val rate: Float? = null,
    val discount: Int? = null,
    val shops: ArrayList<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readArrayList(String::class.java.classLoader) as? ArrayList<String>,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readArrayList(String::class.java.classLoader) as? ArrayList<String>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(lowPrice)
        parcel.writeValue(highPrice)
        parcel.writeArray(subCategory?.toArray())
        parcel.writeValue(rate)
        parcel.writeValue(discount)
        parcel.writeArray(shops?.toArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }
}