package com.alpha.shoplex.model.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.RvHomeAdcardviewBinding
import com.alpha.shoplex.model.pojo.Product
import com.alpha.shoplex.view.activities.DetailsActivity

class AdvertisementsAdapter(val advertisements: ArrayList<Product>) :
    RecyclerView.Adapter<AdvertisementsAdapter.ProductViewHolder>() {

    init {
        advertisements.shuffle()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            RvHomeAdcardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(advertisements[position])

    override fun getItemCount() = advertisements.size

    inner class ProductViewHolder(val binding: RvHomeAdcardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            val context: Context = binding.root.context

            Glide.with(binding.root.context).load(product.images.firstOrNull())
                .error(R.drawable.init_img).into(binding.imgAdvertisement)
            binding.product = product

            binding.tvOffer.visibility = if(product.discount == 0F) View.INVISIBLE else View.VISIBLE

            itemView.setOnClickListener {
                binding.root.context.startActivity(
                    Intent(
                        itemView.context,
                        DetailsActivity::class.java
                    ).apply {
                        this.putExtra(context.getString(R.string.productId), product.productID)
                    })
            }
        }
    }
}