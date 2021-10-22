package com.alpha.shoplex.room.repository

import androidx.lifecycle.LiveData
import com.alpha.shoplex.model.pojo.ProductCart
import com.alpha.shoplex.room.data.ShopLexDao

class CartRepo(private val shopLexDao: ShopLexDao) {

    val readCart: LiveData<List<ProductCart>> = shopLexDao.readCart()

    suspend fun addCart(cart: ProductCart) {
        shopLexDao.addCart(cart)
    }

    suspend fun deleteCart(productID: String) {
        shopLexDao.deleteCart(productID)
    }

    suspend fun updateCart(productID: String, quantity: Int) {
        shopLexDao.updateCart(productID, quantity)
    }
}