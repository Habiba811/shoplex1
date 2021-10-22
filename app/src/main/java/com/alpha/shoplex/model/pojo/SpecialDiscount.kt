package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable
import com.alpha.shoplex.model.enumurations.DiscountType

open class SpecialDiscount : Parcelable {
    var discount: Float = 0F
    var discountType: DiscountType = DiscountType.Fixed

    constructor(parcel: Parcel) : this() {
        discount = parcel.readFloat()
        discountType = DiscountType.valueOf(parcel.readString()!!)
    }

    constructor()
    constructor(discount: Float, discountType: DiscountType) {
        this.discount = discount
        this.discountType = discountType
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(discount)
        parcel.writeString(discountType.name)
    }

    companion object CREATOR : Parcelable.Creator<SpecialDiscount> {
        override fun createFromParcel(parcel: Parcel): SpecialDiscount {
            return SpecialDiscount(parcel)
        }

        override fun newArray(size: Int): Array<SpecialDiscount?> {
            return arrayOfNulls(size)
        }
    }
}