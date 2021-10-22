package com.alpha.shoplex.model.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.model.pojo.Product
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.interfaces.StoresListener
import com.alpha.shoplex.model.pojo.Store

class StoresDBModel(val notifier:StoresListener) {

    fun getStoreById(storeId:String){
        FirebaseReferences.storesRef.whereEqualTo("storeID",storeId).get().addOnCompleteListener {values->
            var stores = arrayListOf<Store>()
            for (document: DocumentSnapshot in values.result!!) {
                var store: Store? = document.toObject<Store>()
                if (store != null) {
                    stores.add(store)
                }
            }
            this.notifier.onStoreInfoReady(stores)
        }
    }

    fun getStores(category: String){
        FirebaseReferences.productsRef.whereEqualTo("category", category)
            .addSnapshotListener { values, _ ->
                val products = arrayListOf<Product>()
                for (document: DocumentSnapshot in values?.documents!!) {
                    val product: Product? = document.toObject<Product>()
                    if (product != null) {
                        products.add(product)
                    }
                }

                val list = products.groupBy { it.storeID }.map { it.key }

                if(list.size == 1)
                    this.notifier.onStoresIDsReady(arrayListOf(list.first()))
                else if(products.size > 1)
                    this.notifier.onStoresIDsReady(list.toList() as ArrayList<String>)
            }
    }
}