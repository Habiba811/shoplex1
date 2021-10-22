package com.alpha.shoplex.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityCheckOutBinding
import com.alpha.shoplex.model.adapter.CheckoutAdapter
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.ProductQuantity
import com.alpha.shoplex.viewmodel.CheckoutFactory
import com.alpha.shoplex.viewmodel.CheckoutVM

class CheckOutActivity : AppCompatActivity(), DroidListener {
    lateinit var binding: ActivityCheckOutBinding
    lateinit var checkoutVM: CheckoutVM

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)

        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DroidNet.getInstance().addInternetConnectivityListener(this)

        setSupportActionBar(binding.toolbarcheckout)
        checkoutVM = ViewModelProvider(this, CheckoutFactory(this)).get(CheckoutVM::class.java)
        checkoutVM.productQuantities =
            intent.getParcelableArrayListExtra<ProductQuantity>(PRODUCTS_QUANTITY) as ArrayList<ProductQuantity>

        if (intent.hasExtra(PRODUCT_PROPERTIES))
            checkoutVM.productProperties = intent.getStringArrayListExtra(PRODUCT_PROPERTIES)

        supportActionBar?.apply {
            title = getString(R.string.Checkout)
            //setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        if (checkoutVM.getAllProducts().isNullOrEmpty()) {
            if (!intent.hasExtra("isBuyNow")) {
                checkoutVM.getAllCartProducts()
            } else {
                val product = checkoutVM.productQuantities.firstOrNull()
                if (product != null)
                    checkoutVM.getProductByID(product.productID)
                else {
                    val snackbar = Snackbar.make(
                        binding.root,
                        binding.root.context.getString(R.string.Product_not_found),
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
        }

        val checkoutAdapter = CheckoutAdapter(this, supportFragmentManager)
        binding.tabLayoutCheckout.setupWithViewPager(binding.viewPagerCheckout)

        binding.viewPagerCheckout.adapter = checkoutAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PRODUCTS_QUANTITY = "PRODUCTS_QUANTITY"
        const val PRODUCT_PROPERTIES = "PRODUCT_PROPERTIES"
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            binding.spinKit.visibility = View.INVISIBLE
            binding.tvLoadCheck.visibility = View.INVISIBLE
            //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
//            Toast.makeText(this, "Sorry but this activity require network connectivity please connect and try again", Toast.LENGTH_SHORT).show()
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            binding.spinKit.visibility = View.VISIBLE
            binding.tvLoadCheck.visibility = View.VISIBLE
        }
    }
}