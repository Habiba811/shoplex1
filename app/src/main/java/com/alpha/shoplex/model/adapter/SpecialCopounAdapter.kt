package com.alpha.shoplex.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpha.shoplex.databinding.SpecialCopounBinding
import com.alpha.shoplex.model.pojo.SpecialCoupon

class SpecialCouponAdapter(val specialCoupons: ArrayList<SpecialCoupon>) :
    RecyclerView.Adapter<SpecialCouponAdapter.SpecialCouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialCouponViewHolder {
        return SpecialCouponViewHolder(
            SpecialCopounBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SpecialCouponViewHolder, position: Int) =
        holder.bind(specialCoupons[position])

    override fun getItemCount() = specialCoupons.size

    inner class SpecialCouponViewHolder(val binding: SpecialCopounBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(specialCoupon: SpecialCoupon) {
            binding.specialCoupon = specialCoupon
        }
    }
}