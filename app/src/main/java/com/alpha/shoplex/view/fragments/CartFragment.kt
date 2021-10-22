package com.alpha.shoplex.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FragmentCartBinding
import com.alpha.shoplex.model.adapter.CartAdapter
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.pojo.ProductQuantity
import com.alpha.shoplex.room.viewmodel.CartViewModel
import com.alpha.shoplex.view.activities.CheckOutActivity
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter


class CartFragment : Fragment(), FavouriteCartListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel
    private var productsQuantity: ArrayList<ProductQuantity> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.btnCheckout.setOnClickListener {
            if (UserInfo.userID != null) {
                if (ArchLifecycleApp.isInternetConnected) {
                    if (productsQuantity.isEmpty()) {
                        val snackbar = Snackbar.make(
                            binding.root,
                            binding.root.context.getString(R.string.emptyCart),
                            Snackbar.LENGTH_LONG
                        )
                        val sbView: View = snackbar.view
                        sbView.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.blueshop
                            )
                        )
                        snackbar.show()
                    }
                    else {
                        startActivity(Intent(context, CheckOutActivity::class.java).apply {
                            this.putParcelableArrayListExtra("PRODUCTS_QUANTITY", productsQuantity)
                        })
                    }
                } else {
                    val snackbar = Snackbar.make(
                        binding.root,
                        binding.root.context.getString(R.string.NoInternetConnection),
                        Snackbar.LENGTH_LONG
                    )
                    val sbView: View = snackbar.view
                    sbView.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.blueshop
                        )
                    )
                    snackbar.show()

                }
            } else {
                val snackbar = Snackbar.make(
                    binding.root,
                    binding.root.context.getString(R.string.pleaseLogin),
                    Snackbar.LENGTH_LONG
                )
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blueshop
                    )
                )
                snackbar.show()

            }

        }

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        getAllCartProducts()

        return binding.root
    }

    private fun getAllCartProducts() {
        val cartAdapter = CartAdapter(this)
        binding.rvCart.adapter =
            ScaleInAnimationAdapter(SlideInBottomAnimationAdapter(cartAdapter)).apply {
                setDuration(700)
                setInterpolator(OvershootInterpolator(2f))
            }
        //binding.rvCart.adapter = cartAdapter
        cartViewModel.readAllCart.observe(viewLifecycleOwner, {
            if (it.count() > 0) {
                binding.noItem.visibility = View.INVISIBLE
            } else {
                binding.noItem.visibility = View.VISIBLE
            }

            cartAdapter.setData(it)
            binding.tvPrice.text = it.map { product ->
                product.cartQuantity * product.newPrice
            }.sum().toString()

            productsQuantity.clear()
            it.forEach { product ->
                productsQuantity.add(ProductQuantity(product.productID, product.cartQuantity))
            }
        })
    }

    override fun onDeleteFromCart(productID: String) {
        super.onDeleteFromCart(productID)
        cartViewModel.deleteCart(productID)
    }

    override fun onUpdateCartQuantity(productID: String, quantity: Int) {
        cartViewModel.updateCart(productID, quantity)
    }
}