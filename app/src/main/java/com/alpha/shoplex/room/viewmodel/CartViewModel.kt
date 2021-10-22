package com.alpha.shoplex.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.alpha.shoplex.model.pojo.ProductCart
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.CartRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(app: Application) : AndroidViewModel(app) {
    val readAllCart: LiveData<List<ProductCart>>
    private val cartRepo: CartRepo

    init {
        val cartDao = ShopLexDataBase.getDatabase(app).shopLexDao()
        cartRepo = CartRepo(cartDao)
        readAllCart = cartRepo.readCart
    }

    fun addCart(cart: ProductCart) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepo.addCart(cart)
        }
    }

    fun deleteCart(productID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepo.deleteCart(productID)
        }
    }

    fun updateCart(productID: String, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepo.updateCart(productID, quantity)
        }
    }
}