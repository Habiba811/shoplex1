package com.alpha.shoplex.model.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

@Entity(tableName = "Cart")
data class ProductCart(
    @Nullable
    @Ignore
    var specialDiscount: SpecialDiscount? = null,
    @Exclude
    var cartQuantity: Int = 1,
    @Ignore
    var shipping: Float = 0F,
    @Exclude
    @get:Exclude
    @Ignore
    var product: Product = Product(),
    @Exclude
    @set:Exclude
    @get:Exclude
    @PrimaryKey
    var id: String = ""
): Product(), Parcelable {

    constructor(product: Product, cartQuantity: Int, specialDiscount: SpecialDiscount?) :
            this(
                specialDiscount,
                cartQuantity,
                product = product
            )

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
        this.quantity = product.quantity
        this.storeLocation = product.storeLocation
    }

    constructor(parcel: Parcel) : this() {
        quantity = parcel.readInt()
        specialDiscount = parcel.readParcelable(SpecialDiscount::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(quantity)
        parcel.writeParcelable(specialDiscount, 1)

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