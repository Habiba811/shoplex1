package com.alpha.shoplex.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.model.enumurations.DeliveryMethod
import com.alpha.shoplex.model.enumurations.DiscountType
import com.alpha.shoplex.model.enumurations.PaymentMethod
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.maps.LocationManager
import com.alpha.shoplex.model.maps.RouteInfo
import com.alpha.shoplex.model.pojo.*
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.FavoriteCartRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CheckoutVM(val context: Context) : ViewModel(), FavouriteCartListener {
    var deliveryMethod: MutableLiveData<DeliveryMethod> = MutableLiveData()
    var paymentMethod: MutableLiveData<PaymentMethod> = MutableLiveData()
    var deliveryLocation: MutableLiveData<Location> = MutableLiveData()
    var deliveryAddress: MutableLiveData<String> = MutableLiveData()
    var subTotalPrice: MutableLiveData<Float> = MutableLiveData()
    var totalDiscount: MutableLiveData<Float> = MutableLiveData()
    var shipping: MutableLiveData<Float> = MutableLiveData()
    var totalPrice: MutableLiveData<Float> = MutableLiveData()
    var coupons: MutableLiveData<Float> = MutableLiveData()
    val isAllProductsReady: MutableLiveData<Boolean> = MutableLiveData()
    var productProperties: ArrayList<String>? = null

    private var repo: FavoriteCartRepo
    private var lifecycleScope: CoroutineScope

    private var products: ArrayList<ProductCart> = arrayListOf()
    var productQuantities: ArrayList<ProductQuantity> = arrayListOf()

    init {
        lifecycleScope = (context as AppCompatActivity).lifecycleScope
        repo = FavoriteCartRepo(ShopLexDataBase.getDatabase(context).shopLexDao())

        deliveryMethod.value = DeliveryMethod.Door
        paymentMethod.value = PaymentMethod.Cash
        deliveryLocation.value = UserInfo.location
        deliveryAddress.value = UserInfo.address
        subTotalPrice.value = 0F
        totalDiscount.value = 0F
        shipping.value = 0F
        totalPrice.value = 0F
        coupons.value = 0F
        isAllProductsReady.value = false
    }

    fun getAllCartProducts() {
        FirebaseReferences.usersRef.document(UserInfo.userID!!)
            .collection("Lists")
            .document("Cart").get().addOnSuccessListener { result ->
                val cartList: ArrayList<String> = result.get("cartList") as ArrayList<String>
                for (productID in cartList) {
                    FirebaseReferences.productsRef.document(productID).get()
                        .addOnSuccessListener { productResult ->
                            if (productResult != null) {
                                val prod = productResult.toObject<ProductCart>()
                                FirebaseReferences.productsRef.document(productID)
                                    .collection("Special Discounts")
                                    .document(UserInfo.userID!!).get().addOnSuccessListener {
                                        var specialDiscount: SpecialDiscount? = null
                                        if (it.exists()) {
                                            specialDiscount = it.toObject()
                                        }
                                        prod?.cartQuantity =
                                            productQuantities.find { product -> product.productID == prod?.productID }?.quantity
                                                ?: 1

                                        val productCart =
                                            ProductCart(prod!!, prod.cartQuantity, specialDiscount)

                                        if (prod.quantity == 0) {
                                            Toast.makeText(
                                                context,
                                                "Product ${prod.name} out of stock",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onDeleteFromFavourite(prod.productID)
                                        } else {
                                            if (prod.cartQuantity > prod.quantity) {
                                                Toast.makeText(
                                                    context,
                                                    "${prod.cartQuantity - prod.quantity} items of product ${prod.name} was solid remains ${prod.quantity}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onUpdateCartQuantity(prod.productID, prod.quantity)
                                                prod.cartQuantity = prod.quantity
                                            }
                                            addProduct(productCart)
                                        }
                                    }
                            }
                        }
                }
            }
    }

    fun getProductByID(productID: String) {
        FirebaseReferences.productsRef.document(productID).get()
            .addOnSuccessListener { productResult ->
                if (productResult.exists()) {
                    val prod = productResult.toObject<ProductCart>()
                    FirebaseReferences.productsRef.document(productID)
                        .collection("Special Discounts")
                        .document(UserInfo.userID!!).get().addOnSuccessListener {
                            var specialDiscount: SpecialDiscount? = null
                            if (it.exists()) {
                                specialDiscount = it.toObject()
                            }

                            val productCart =
                                ProductCart(prod!!, 1, specialDiscount)

                            if (prod.quantity == 0) {
                                Toast.makeText(
                                    context,
                                    "Product ${prod.name} out of stock",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDeleteFromFavourite(prod.productID)
                            } else {
                                addProduct(productCart)
                            }
                        }
                }
            }
    }

    fun addProduct(productCart: ProductCart) {
        this.products.add(productCart)
        this.subTotalPrice.value =
            this.subTotalPrice.value?.plus((productCart.cartQuantity * productCart.newPrice))
        var discount = 0F
        if (productCart.specialDiscount != null) {
            discount = if (productCart.specialDiscount!!.discountType == DiscountType.Fixed) {
                productCart.specialDiscount!!.discount
            } else {
                productCart.price * (productCart.specialDiscount!!.discount / 100)
            }

            this.coupons.value = this.coupons.value?.plus(discount)
        } else if (productCart.price != productCart.newPrice) {
            discount = (productCart.price - productCart.newPrice)
        }

        discount = "%.2f".format(discount).toFloat()
        productCart.discount = discount
        productCart.newPrice = "%.2f".format(productCart.price - productCart.discount).toFloat()

        addShipping(productCart)
        this.totalDiscount.value =
            this.totalDiscount.value?.plus((productCart.cartQuantity * discount))
        this.totalPrice.value =
            "%.2f".format(subTotalPrice.value?.minus(totalDiscount.value!!)).toFloat()
    }

    fun getAllProducts(): ArrayList<ProductCart> {
        return products
    }

    private fun addShipping(productCart: ProductCart) {
        GlobalScope.launch(Dispatchers.IO) {
            val info: RouteInfo? = LocationManager.getInstance(context).getRouteInfo(
                deliveryLocation.value!!,
                productCart.storeLocation
            )

            var res = "N/A"

            if (info != null) {
                res = info.distance!!
            }
            val cost = calcShipping(res)

            totalPrice.postValue(totalPrice.value?.plus(cost))
            shipping.postValue(shipping.value?.plus(cost))
            productCart.shipping = cost.toFloat()
            if (productCart == products.last()) {
                isAllProductsReady.postValue(true)
            }
        }
    }

    fun reAddShipping() {
        this.totalPrice.postValue(totalPrice.value?.minus(this.shipping.value!!))
        this.shipping.value = 0F
        this.isAllProductsReady.value = false
        for (product in products) {
            addShipping(product)
        }
    }

    private fun calcShipping(distance: String): Int {
        return if (distance.contains("km", true)) {
            (5 * distance.split(" ")[0].toFloat()).toInt()
        } else {
            15
        }
    }

    override fun onUpdateCartQuantity(productID: String, quantity: Int) {
        lifecycleScope.launch {
            repo.updateCart(productID, quantity)
        }
    }

    override fun onDeleteFromCart(productID: String) {
        super.onDeleteFromCart(productID)
        lifecycleScope.launch {
            repo.deleteCart(productID)
        }
    }
}