package com.alpha.shoplex.model.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.ProductsListener
import com.alpha.shoplex.model.pojo.Order
import com.alpha.shoplex.model.pojo.Product

class OrdersDBModel(val notifier: ProductsListener) {

    fun getCurrentOrders() {
        FirebaseReferences.ordersRef.whereEqualTo("userID", UserInfo.userID)
            .whereEqualTo("orderStatus", "Current")
            .addSnapshotListener { values, _ ->
                var orders = arrayListOf<Order>()
                for (document: DocumentSnapshot in values?.documents!!) {
                    var order: Order? = document.toObject<Order>()
                    if (order != null) {
                        orders.add(order)
                        FirebaseReferences.productsRef.document(order.productID).get()
                            .addOnSuccessListener {
                                order.product = it.toObject<Product>()
                                if (document == values.last()) {
                                    this.notifier.onCurrentOrdersReady(orders)
                                }
                            }
                    }
                }
            }
    }

    fun getLastOrders() {
        FirebaseReferences.ordersRef.whereEqualTo("userID", UserInfo.userID).whereIn(
            "orderStatus",
            listOf("Delivered", "Canceled")
        ).addSnapshotListener { values, _ ->
                val orders = arrayListOf<Order>()
                for (document: DocumentSnapshot in values?.documents!!) {
                    val order: Order? = document.toObject<Order>()
                    if (order != null) {
                        orders.add(order)
                        FirebaseReferences.productsRef.document(order.productID).get().addOnSuccessListener {
                                order.product = it.toObject<Product>()
                                if (document == values.last()) {
                                    this.notifier.onLastOrdersReady(orders)
                                }
                            }
                    }
                }
            }
    }
}