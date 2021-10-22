package com.alpha.shoplex.room.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.Product
import com.alpha.shoplex.model.pojo.ProductCart
import com.alpha.shoplex.model.pojo.ProductFavourite
import com.alpha.shoplex.model.pojo.StoreLocationInfo
import com.alpha.shoplex.room.data.ShopLexDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteCartRepo(private val shopLexDao: ShopLexDao) {

    var productID = MutableStateFlow("")
    var storeIfo = MutableStateFlow(StoreLocationInfo())
    var storesIDs = MutableStateFlow(ArrayList<String>())

    // Favorite
    val favoriteProducts: LiveData<List<ProductFavourite>> = shopLexDao.readFavourite()

    val searchFavouriteByID = productID.flatMapLatest {
        shopLexDao.searchFav(it)
    }.asLiveData()

    val storeLocationInfo = storeIfo.flatMapLatest {
        shopLexDao.getLocation(it.storeID, it.location)
    }.asLiveData()

    val storesLocationInfo = storesIDs.flatMapLatest {
        shopLexDao.getLocations(it)
    }.asLiveData()

    suspend fun addFavourite(favourite: ProductFavourite) {
        shopLexDao.addFavourite(favourite)
    }

    suspend fun deleteFavourite(productID: String) {
        shopLexDao.deleteFavourite(productID)
    }

    // Cart
    val cartProducts: LiveData<List<ProductCart>> = shopLexDao.readCart()

    val searchCartByID = productID.flatMapLatest {
        shopLexDao.searchCart(it)
    }.asLiveData()

    suspend fun addCart(cart: ProductCart) {
        shopLexDao.addCart(cart)
    }

    suspend fun deleteCart(productID: String) {
        shopLexDao.deleteCart(productID)
    }

    suspend fun updateCart(productID: String, quantity: Int) {
        shopLexDao.updateCart(productID, quantity)
    }

    suspend fun addNewLocation(location: StoreLocationInfo) {
        shopLexDao.addLocation(location)
    }

    suspend fun syncProducts(context: Context) {

        FirebaseReferences.usersRef.document(UserInfo.userID!!).collection("Lists")
            .get().addOnSuccessListener {

                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        favoriteProducts.observe(context as AppCompatActivity, { favProducts ->
                            val favProductsList = favProducts.groupBy { productFav ->
                                productFav.productID
                            }.map { mapEntry ->
                                mapEntry.key
                            }.toTypedArray()

                            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                                .collection("Lists")
                                .document("Favorite")
                                .update("favoriteList", FieldValue.arrayUnion(*favProductsList))
                        })
                    }
                }

                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        cartProducts.observe(context as AppCompatActivity, { cartProducts ->
                            val cartProductsList = cartProducts.groupBy { productFav ->
                                productFav.productID
                            }.map { mapEntry ->
                                mapEntry.key
                            }.toTypedArray()

                            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                                .collection("Lists")
                                .document("Cart")
                                .update("cartList", FieldValue.arrayUnion(*cartProductsList))
                        })
                    }
                }

                for (document in it.documents) {
                    if (document.reference.id == "Cart") {
                        val cartList = document.get("cartList") as ArrayList<String>

                        for (cartID in cartList) {
                            FirebaseReferences.productsRef.document(cartID).get()
                                .addOnSuccessListener { result ->
                                    if (result.exists()) {
                                        val product = result.toObject<Product>()
                                        GlobalScope.launch {
                                            addCart(ProductCart(product = product!!))
                                        }
                                    }
                                }
                        }
                    }

                    if (document.reference.id == "Favorite") {
                        val favoriteList = document.get("favoriteList") as ArrayList<String>

                        for (favID in favoriteList) {
                            FirebaseReferences.productsRef.document(favID).get()
                                .addOnSuccessListener { result ->
                                    if (result.exists()) {
                                        val product = result.toObject<Product>()
                                        GlobalScope.launch {
                                            addFavourite(ProductFavourite(product!!))
                                        }
                                    }
                                }
                        }
                    }
                }
            }
    }
}