package com.alpha.shoplex.model.pojo

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.alpha.shoplex.model.enumurations.DeliveryMethod
import com.alpha.shoplex.model.enumurations.OrderStatus
import com.alpha.shoplex.model.enumurations.PaymentMethod
import com.alpha.shoplex.model.extra.UserInfo
import kotlin.collections.ArrayList

class Order {
    val orderID: String = Timestamp.now().toDate().time.toString()
    var productID: String = ""
    var userID: String = ""
    var storeID: String = ""
    var storeName: String = ""
    var orderStatus: OrderStatus = OrderStatus.Current
    var quantity: Int = 1
    var specialDiscount: SpecialDiscount? = null

    var productPrice: Float = 0.0F
    var subTotalPrice: Float = 0F
    var totalDiscount: Float = 0F
    var shipping: Float = 0F
    var totalPrice: Float = 0F

    @Exclude
    @set:Exclude
    @get:Exclude
    var product: Product? = null

    var deliveryMethod: String = DeliveryMethod.Door.name
    var paymentMethod: String = PaymentMethod.Cash.name
    var deliveryLoc: Location? = null
    var deliveryAddress: String = ""

    var orderProperties: ArrayList<String>? = null

    constructor()

    constructor(product: ProductCart, orderProperties: ArrayList<String>? = null) {
        this.productID = product.productID
        this.userID = UserInfo.userID!!
        this.storeID = product.storeID
        this.storeName = product.storeName
        this.orderStatus = OrderStatus.Current
        this.quantity = product.cartQuantity
        this.specialDiscount = product.specialDiscount

        this.productPrice = product.price
        this.subTotalPrice = "%.2f".format(product.price * product.cartQuantity).toFloat()
        this.totalDiscount = "%.2f".format(product.discount * product.cartQuantity).toFloat()
        this.shipping = product.shipping
        this.totalPrice = this.subTotalPrice + this.shipping - this.totalDiscount

        this.orderProperties = orderProperties
    }
}