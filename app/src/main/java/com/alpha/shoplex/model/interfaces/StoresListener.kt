package com.alpha.shoplex.model.interfaces

import com.alpha.shoplex.model.pojo.Store

interface StoresListener {
    fun onStoreInfoReady(stores: ArrayList<Store>){}
    fun onStoresIDsReady(storesIDs: ArrayList<String>){}
}