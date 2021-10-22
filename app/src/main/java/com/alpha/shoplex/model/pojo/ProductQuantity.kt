package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable

class ProductQuantity(val productID: String, val quantity: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productID)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductQuantity> {
        override fun createFromParcel(parcel: Parcel): ProductQuantity {
            return ProductQuantity(parcel)
        }

        override fun newArray(size: Int): Array<ProductQuantity?> {
            return arrayOfNulls(size)
        }
    }
}