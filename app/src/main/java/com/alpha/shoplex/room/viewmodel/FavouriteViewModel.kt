package com.alpha.shoplex.room.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.shoplex.model.pojo.ProductFavourite
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.FavouriteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteViewModel(context: Context) : ViewModel() {
    val readAllFavourite: LiveData<List<ProductFavourite>>
    var searchFavourite: LiveData<ProductFavourite>
    private val favouriteRepo:FavouriteRepo

    init {
        val favouriteDao = ShopLexDataBase.getDatabase(context).shopLexDao()
        favouriteRepo = FavouriteRepo(favouriteDao)
        readAllFavourite = favouriteRepo.readFavourite
        searchFavourite = favouriteRepo.searchFavourite
    }

    fun addFavourite(favourite: ProductFavourite) {
        viewModelScope.launch(Dispatchers.IO) {
            favouriteRepo.addFavourite(favourite)
        }
    }

    fun deleteFavourite(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            favouriteRepo.deleteFavourite(productId)
        }
    }

    fun searchFavourite(productId:String){
        viewModelScope.launch(Dispatchers.IO) {
            favouriteRepo.productID.value = productId
        }
    }
}