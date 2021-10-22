package com.alpha.shoplex.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FragmentPaymentBinding
import com.alpha.shoplex.model.adapter.SpecialCouponAdapter
import com.alpha.shoplex.model.enumurations.PaymentMethod
import com.alpha.shoplex.model.pojo.SpecialCoupon
import com.alpha.shoplex.view.activities.CheckOutActivity
import com.alpha.shoplex.viewmodel.CheckoutVM

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var checkoutVM: CheckoutVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        checkoutVM = (activity as CheckOutActivity).checkoutVM

        binding.rgPayment.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbCash.id -> checkoutVM.paymentMethod.value = PaymentMethod.Cash
                binding.rbVisaMaster.id -> checkoutVM.paymentMethod.value =
                    PaymentMethod.Visa_Master
            }
        }

        checkoutVM.isAllProductsReady.observe(viewLifecycleOwner, {
            if(it){
                //var coupon = 0.0F
                    val specialCoupons: ArrayList<SpecialCoupon> = arrayListOf()
                for (product in checkoutVM.getAllProducts()) {
                    if(product.specialDiscount != null){
                        specialCoupons.add(SpecialCoupon(product.name, product.specialDiscount!!.discount, product.specialDiscount!!.discountType))
                    }
                    /*
                    val discountValue = product.specialDiscount?.discount

                    when (discount.specialDiscount?.discountType) {
                        DiscountType.Fixed -> coupon += discountValue!!
                        DiscountType.Percentage -> coupon += discountValue!!
                    }
                    */
                }
                //binding.tvCopoun.text = coupon.toString()
                if(specialCoupons.isNotEmpty()){
                    binding.rvSpecialCoupons.adapter = SpecialCouponAdapter(specialCoupons)
                }else{
                    binding.tvSpecialCoupon.text = getString(R.string.DontHaveSpecialCoupons)
                }
            }
        })

        checkoutVM.subTotalPrice.observe(viewLifecycleOwner, {
            binding.tvSubtotalPrice.text = getString(R.string.EGP).format(it)
        })

        checkoutVM.totalDiscount.observe(viewLifecycleOwner, {
            binding.tvDiscountPrice.text = getString(R.string.EGP).format(it)
        })

        checkoutVM.shipping.observe(viewLifecycleOwner, {
            binding.tvShippingPrice.text = getString(R.string.EGP).format(it)
        })

        checkoutVM.totalPrice.observe(viewLifecycleOwner, {
            binding.tvTotalPrice.text = getString(R.string.EGP).format(it)
        })

        //binding.tvCopoun.text = getString(R.string.DiscountBy).format(checkoutVM.coupons.value)
        binding.btnPayment.setOnClickListener {

            val pager = (activity as CheckOutActivity).binding.viewPagerCheckout
            pager.currentItem = pager.currentItem + 1
        }

        return binding.root
    }
}