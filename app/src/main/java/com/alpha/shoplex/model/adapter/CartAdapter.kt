package com.alpha.shoplex.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.RvCartHomeBinding
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.pojo.ProductCart


class CartAdapter(
    var favouriteCartListener: FavouriteCartListener
) :
    RecyclerView.Adapter<CartAdapter.ProductViewHolder>() {

    var carts = emptyList<ProductCart>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            RvCartHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(carts[position])

    override fun getItemCount() = carts.size
    fun setData(product: List<ProductCart>) {
        this.carts = product as ArrayList<ProductCart>
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(val binding: RvCartHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductCart) {
            Glide.with(binding.root.context).load(product.images.firstOrNull()).error(R.drawable.init_img).into(binding.imgCart)

            binding.product = product
            binding.imgDelete.setOnClickListener {
                val builder = binding.root.context?.let { AlertDialog.Builder(it) }
                builder?.setTitle(binding.root.context.getString(R.string.delete))
                builder?.setMessage(binding.root.context.getString(R.string.deleteMessage))

                builder?.setPositiveButton(binding.root.context.getString(R.string.yes)) { _, _ ->
                    favouriteCartListener.onDeleteFromCart(product.productID)
                    val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.Success), Snackbar.LENGTH_LONG)
                    val sbView: View = snackbar.view
                    sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                    snackbar.show()

                }
                builder?.setNegativeButton(binding.root.context.getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }

                builder?.show()
            }

            binding.btnMinus.setOnClickListener {
                if (product.cartQuantity > 1) {
                    product.cartQuantity--
                    favouriteCartListener.onUpdateCartQuantity(
                        product.productID,
                        product.cartQuantity--
                    )
                }
            }
            binding.btnPlus.setOnClickListener {
                if (product.cartQuantity < product.quantity) {
                    product.cartQuantity++
                    favouriteCartListener.onUpdateCartQuantity(
                        product.productID,
                        product.cartQuantity
                    )
                } else {
                    val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.max), Snackbar.LENGTH_LONG)
                    val sbView: View = snackbar.view
                    sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                    snackbar.show()
                }
            }
        }
    }
}