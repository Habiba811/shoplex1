package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable

class Sort(
    val price: Boolean? = false,
    val rate: Boolean = false,
    val discount: Boolean = false,
    val nearestShop: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(price)
        parcel.writeByte(if (rate) 1 else 0)
        parcel.writeByte(if (discount) 1 else 0)
        parcel.writeByte(if (nearestShop) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sort> {
        override fun createFromParcel(parcel: Parcel): Sort {
            return Sort(parcel)
        }

        override fun newArray(size: Int): Array<Sort?> {
            return arrayOfNulls(size)
        }
    }
}