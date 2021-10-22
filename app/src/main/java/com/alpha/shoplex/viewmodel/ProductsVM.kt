package com.alpha.shoplex.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alpha.shoplex.model.enumurations.Category
import com.alpha.shoplex.model.firebase.ProductsDBModel
import com.alpha.shoplex.model.interfaces.ProductsListener
import com.alpha.shoplex.model.pojo.*

class ProductsVM : ViewModel(), ProductsListener {
    var products: MutableLiveData<ArrayList<Product>> = MutableLiveData()
    var advertisements: MutableLiveData<ArrayList<Product>> = MutableLiveData()
    private var productsDBModel = ProductsDBModel(this)
    var reviews: MutableLiveData<ArrayList<Review>> = MutableLiveData()
    val reviewStatistics: MutableLiveData<ReviewStatistics> = MutableLiveData()

    var filter: MutableLiveData<Filter> = MutableLiveData()
    var sort: MutableLiveData<Sort?> = MutableLiveData()

    var productID: MutableLiveData<String> = MutableLiveData()

    fun getAllProducts(category: String, filter: Filter, sort: Sort? = null) {
        this.filter.value = filter
        this.sort.value = sort
        productsDBModel.getAllProducts(category, filter, sort)
    }

    fun getProduct() {
        if (productID.value != null)
            productsDBModel.getProductById(productID.value!!)
    }

    fun getCategories(): Array<String> {
        return Category.values().map {
            it.toString().split("_").joinToString(" ")
        }.toTypedArray()
    }

    fun getAllPremiums() {
        productsDBModel.getAllPremiums()
    }

    fun getReviews() {
        if (productID.value != null) {
            productsDBModel.getReviewsStatistics(productID.value!!)
            productsDBModel.getReviewByProductId(productID.value!!)
        }
    }

    override fun onAllProductsReady(products: ArrayList<Product>) {
        this.products.value = products
    }

    override fun onAllAdvertisementsReady(products: ArrayList<Product>) {
        this.advertisements.value = products
    }

    override fun onAllReviewsReady(reviews: ArrayList<Review>) {
        this.reviews.value = reviews
    }

    override fun onReviewStatisticsReady(reviewStatistics: ReviewStatistics) {
        this.reviewStatistics.value = reviewStatistics
    }
}