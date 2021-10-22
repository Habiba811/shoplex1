package com.alpha.shoplex.model.interfaces

import com.google.firebase.firestore.FieldValue
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.ProductCart
import com.alpha.shoplex.model.pojo.ProductFavourite
import com.alpha.shoplex.model.pojo.StoreLocationInfo

interface FavouriteCartListener {
    // Favourite
    fun onAddToFavourite(productFavourite: ProductFavourite){
        if(UserInfo.userID != null) {
            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                .collection("Lists")
                .document("Favorite")
                 .update("favoriteList" , FieldValue.arrayUnion(productFavourite.productID))
        }
    }

    fun onDeleteFromFavourite(productID: String){
        if(UserInfo.userID != null) {
            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                .collection("Lists")
                .document("Favorite")
                .update("favoriteList" , FieldValue.arrayRemove(productID))
        }
    }

    // Cart
    fun onAddToCart(productCart:ProductCart){
        if(UserInfo.userID != null) {
            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                .collection("Lists")
                .document("Cart")
                .update("cartList", FieldValue.arrayUnion(productCart.productID))
        }
    }

    fun onDeleteFromCart(productID: String){
        if(UserInfo.userID != null) {
            FirebaseReferences.usersRef.document(UserInfo.userID!!)
                .collection("Lists")
                .document("Cart")
                .update("cartList", FieldValue.arrayRemove(productID))
        }
    }

    fun onUpdateCartQuantity(productID: String, quantity: Int){}

    fun onSearchForFavouriteCart(productId:String){}

    fun onAddStoreInfo(storeLocationInfo: StoreLocationInfo){}

    fun onFindingRoute(storeLocation: StoreLocationInfo){}
}