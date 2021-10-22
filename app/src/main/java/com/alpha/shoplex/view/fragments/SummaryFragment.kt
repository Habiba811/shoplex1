package com.alpha.shoplex.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FragmentSummaryBinding
import com.alpha.shoplex.model.adapter.SummaryAdapter
import com.alpha.shoplex.model.enumurations.PaymentMethod
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.PaymentListener
import com.alpha.shoplex.model.pojo.Order
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.FavoriteCartRepo
import com.alpha.shoplex.view.activities.CheckOutActivity
import com.alpha.shoplex.viewmodel.CheckoutVM
import com.alpha.shoplex.viewmodel.PaymentMethodFactory
import com.alpha.shoplex.viewmodel.PaymentMethodVM
import kotlinx.coroutines.launch

class SummaryFragment : Fragment(), PaymentListener {
    private lateinit var binding: FragmentSummaryBinding
    private lateinit var summaryAdapter: SummaryAdapter
    private lateinit var checkoutVM: CheckoutVM
    private lateinit var paymentMethodVM: PaymentMethodVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentMethodVM =
            ViewModelProvider(requireActivity(), PaymentMethodFactory(requireActivity(), this, this)).get(
                PaymentMethodVM::class.java
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        checkoutVM = (activity as CheckOutActivity).checkoutVM

        summaryAdapter = SummaryAdapter(checkoutVM.getAllProducts())
        binding.rvSummary.adapter = summaryAdapter

        checkoutVM.totalPrice.observe(viewLifecycleOwner, {
            binding.tvTotalPrice.text = getString(R.string.EGP).format(it)
        })

        binding.tvItemNum.text = getString(R.string.items).format(checkoutVM.getAllProducts().size)

        checkoutVM.deliveryMethod.observe(viewLifecycleOwner, {
            binding.tvDeliveryStatue.text = it.name.replace("_", " ")
        })

        checkoutVM.paymentMethod.observe(viewLifecycleOwner, {
            binding.tvPaymentStatue.text = it.name.replace("_", "/")
        })

        checkoutVM.isAllProductsReady.observe(viewLifecycleOwner, {
            if (it) {
                binding.btnSummary.isEnabled = true
            }
        })

        binding.btnSummary.setOnClickListener {
            if(checkoutVM.deliveryAddress.value!!.trim().isEmpty() || (checkoutVM.deliveryLocation.value!!.latitude == 0.0 && checkoutVM.deliveryLocation.value!!.longitude == 0.0)){
                Toast.makeText(requireContext(), getString(R.string.check_address), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (checkoutVM.paymentMethod.value == PaymentMethod.Visa_Master) {
                binding.btnSummary.isEnabled = false
                paymentMethodVM.pay(checkoutVM.totalPrice.value!!)
            } else {
                execOrders()
            }
        }

        return binding.root
    }

    private fun execOrders() {
        val products = checkoutVM.getAllProducts()
        for (product in products) {
            val order = Order(product, checkoutVM.productProperties)
            order.deliveryLoc = checkoutVM.deliveryLocation.value
            order.deliveryAddress = checkoutVM.deliveryAddress.value!!
            order.deliveryMethod = checkoutVM.deliveryMethod.value!!.name.replace("_", " ")
            order.paymentMethod = checkoutVM.paymentMethod.value!!.name.replace("_", "/")
            FirebaseReferences.ordersRef.document(order.orderID).set(order)
                .addOnSuccessListener {
                    deleteFromCart(product.productID)
                    if (product == products.last()) {
                        val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.Success), Snackbar.LENGTH_LONG)
                        val sbView: View = snackbar.view
                        sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                        snackbar.show()
                        requireActivity().finish()
                    }

                    if(product.specialDiscount != null){
                        FirebaseReferences.productsRef.document(product.productID).collection("Special Discounts").document(order.userID).delete()
                    }
                }
        }
    }

    private fun deleteFromCart(productID: String) {
        if (UserInfo.userID != null) {
            val repo = FavoriteCartRepo(ShopLexDataBase.getDatabase(requireContext()).shopLexDao())
            lifecycleScope.launch {
                repo.deleteCart(productID)
                FirebaseReferences.usersRef.document(UserInfo.userID!!)
                    .collection("Lists")
                    .document("Cart")
                    .update("cartList", FieldValue.arrayRemove(productID))
            }
        }
    }

    override fun onPaymentComplete() {
        execOrders()
    }

    override fun onPaymentFailedToLoad() {
        val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.fail_payment), Snackbar.LENGTH_LONG)
        val sbView: View = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
        snackbar.show()
        binding.btnSummary.isEnabled = true
    }

    override fun onPaymentCanceledOrFailed() {
        binding.btnSummary.isEnabled = true
    }

    override fun onMinimumPrice() {
        val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.minimum_charge), Snackbar.LENGTH_LONG)
        val sbView: View = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
        snackbar.show()
    }
}