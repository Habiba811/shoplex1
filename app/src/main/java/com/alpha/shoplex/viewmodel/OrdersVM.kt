package com.alpha.shoplex.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alpha.shoplex.model.firebase.OrdersDBModel
import com.alpha.shoplex.model.interfaces.ProductsListener
import com.alpha.shoplex.model.pojo.Order

class OrdersVM : ViewModel(), ProductsListener {

    private val ordersDBModel = OrdersDBModel(this)
    var currentOrders: MutableLiveData<ArrayList<Order>> = MutableLiveData()
    var lastOrders: MutableLiveData<ArrayList<Order>> = MutableLiveData()

    override fun onOrderSuccess() {

    }

    override fun onOrderFailed() {

    }

    override fun onCurrentOrdersReady(orders: ArrayList<Order>) {
        this.currentOrders.value = orders

    }

    override fun onLastOrdersReady(lastOrders: ArrayList<Order>) {
        this.lastOrders.value = lastOrders
    }

    fun getCurrentOrders() {
        ordersDBModel.getCurrentOrders()
    }

    fun getLastOrders() {
        ordersDBModel.getLastOrders()
    }
}