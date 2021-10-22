package com.alpha.shoplex.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alpha.shoplex.model.firebase.StoresDBModel
import com.alpha.shoplex.model.interfaces.StoresListener
import com.alpha.shoplex.model.pojo.Store

class DetailsVM : ViewModel(), StoresListener {
    var store: MutableLiveData<Store> = MutableLiveData()
    private var storesDBModel = StoresDBModel(this)

    fun getStoreData(storeID: String) {
        storesDBModel.getStoreById(storeID)
    }

    override fun onStoreInfoReady(stores: ArrayList<Store>) {
        if (stores.count() > 0) {
            this.store.value = stores.first()
        }
    }
}