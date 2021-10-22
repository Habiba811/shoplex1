package com.alpha.shoplex.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FavouriteItemRowBinding
import com.alpha.shoplex.model.pojo.ProductFavourite
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.pojo.ProductCart
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.FavoriteCartRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FavouriteAdapter :
    RecyclerView.Adapter<FavouriteAdapter.ProductViewHolder>() {

    private var favourites = emptyList<ProductFavourite>()
    private lateinit var context: Context
    private lateinit var repo: FavoriteCartRepo
    private lateinit var lifecycleScope: CoroutineScope

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        context = parent.context
        lifecycleScope = (context as AppCompatActivity).lifecycleScope
        repo = FavoriteCartRepo(ShopLexDataBase.getDatabase(context).shopLexDao())

        return ProductViewHolder(
            FavouriteItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(favourites[position])

    override fun getItemCount() = favourites.size
    fun setData(product: List<ProductFavourite>) {
        this.favourites = product as ArrayList<ProductFavourite>
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(val binding: FavouriteItemRowBinding) :
        RecyclerView.ViewHolder(binding.root), FavouriteCartListener {
        fun bind(product: ProductFavourite) {
            Glide.with(binding.root.context).load(product.images.firstOrNull())
                .error(R.drawable.init_img).into(binding.imgProduct)
            binding.product = product

            repo.searchCartByID.observe(context as AppCompatActivity, {
                if (it == null) {
                    binding.fabAddProduct.setImageDrawable(context.getDrawable(R.drawable.ic_cart))
                    product.isCart = false
                } else {
                    binding.fabAddProduct.setImageDrawable(context.getDrawable(R.drawable.ic_done))
                    product.isCart = true
                }
            })

            onSearchForFavouriteCart(product.productID)

            binding.imgDelete.setOnClickListener {
                val builder = binding.root.context?.let { AlertDialog.Builder(it) }
                builder?.setTitle(binding.root.context.getString(R.string.delete))
                builder?.setMessage(binding.root.context.getString(R.string.deleteMessage))

                builder?.setPositiveButton(binding.root.context.getString(R.string.yes)) { _, _ ->
                            onDeleteFromFavourite(product.productID)
                            notifyDataSetChanged()
                            val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.Success), Snackbar.LENGTH_LONG)
                            val sbView: View = snackbar.view
                            sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                            snackbar.show()

                        }
                builder?.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    dialog.cancel()
                }

                builder?.show()
            }

            binding.fabAddProduct.setOnClickListener {
                if (product.isCart) {
                    onDeleteFromCart(product.productID)
                    binding.fabAddProduct.setImageDrawable(context.getDrawable(R.drawable.ic_cart))
                    product.isCart = false
                } else {
                    onAddToCart(ProductCart(product = product))
                    binding.fabAddProduct.setImageDrawable(context.getDrawable(R.drawable.ic_done))
                    product.isCart = true
                }
            }
        }

        override fun onAddToCart(productCart: ProductCart) {
            super.onAddToCart(productCart)
            productCart.cartQuantity = 1
            lifecycleScope.launch {
                repo.addCart(productCart)
            }
        }

        override fun onDeleteFromCart(productID: String) {
            super.onDeleteFromCart(productID)
            lifecycleScope.launch {
                repo.deleteCart(productID)
            }
        }

        override fun onDeleteFromFavourite(productID: String) {
            super.onDeleteFromFavourite(productID)
            lifecycleScope.launch {
                repo.deleteFavourite(productID)
            }
        }

        override fun onSearchForFavouriteCart(productId: String) {
            repo.productID.value = productId
        }
    }
}