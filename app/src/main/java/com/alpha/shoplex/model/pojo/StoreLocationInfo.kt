package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "StoresLocation", primaryKeys = ["storeID", "location"])
data class StoreLocationInfo(
    val storeID: String = "",
    val location: Location = Location(),
    val storeName: String? = null,
    val distance: String? = null,
    val duration: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Location::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storeID)
        parcel.writeParcelable(location, flags)
        parcel.writeString(storeName)
        parcel.writeString(distance)
        parcel.writeString(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreLocationInfo> {
        override fun createFromParcel(parcel: Parcel): StoreLocationInfo {
            return StoreLocationInfo(parcel)
        }

        override fun newArray(size: Int): Array<StoreLocationInfo?> {
            return arrayOfNulls(size)
        }
    }
}
