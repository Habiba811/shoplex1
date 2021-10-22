package com.alpha.shoplex.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.DialogAddReviewBinding
import com.alpha.shoplex.databinding.OrderItemRowBinding
import com.alpha.shoplex.model.enumurations.OrderStatus
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.Order
import com.alpha.shoplex.model.pojo.Review

class OrderAdapter(var ordersInfo: ArrayList<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) =
        holder.bind(ordersInfo[position])

    override fun getItemCount() = ordersInfo.size

    inner class OrderViewHolder(val binding: OrderItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            if (order.product != null) {
                binding.order = order
                Glide.with(itemView.context).load(order.product!!.images.firstOrNull())
                    .error(R.drawable.init_img).into(binding.imgProduct)
                when (order.orderStatus) {
                    OrderStatus.Current -> binding.tvbutton.text = itemView.context.resources.getString(R.string.cancel)
                    OrderStatus.Delivered -> binding.tvbutton.text = binding.root.context.getString(R.string.Review)
                    else -> binding.tvbutton.visibility = View.INVISIBLE
                }

                binding.tvbutton.setOnClickListener {
                    if (ArchLifecycleApp.isInternetConnected) {
                        if (order.orderStatus == OrderStatus.Current) {
                            val builder = binding.root.context?.let { AlertDialog.Builder(it) }
                            builder?.setTitle(binding.root.context.getString(R.string.cancelOrder))
                            builder?.setMessage(binding.root.context.getString(R.string.cancelMessage))

                            builder?.setPositiveButton(binding.root.context.getString(R.string.yes)) { _, _ ->
                                FirebaseReferences.ordersRef.document(order.orderID)
                                    .update("orderStatus", OrderStatus.Canceled).addOnSuccessListener {
                                        val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.Success), Snackbar.LENGTH_LONG)
                                        val sbView: View = snackbar.view
                                        sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                                        snackbar.show()
                                        ordersInfo.removeAt(bindingAdapterPosition)
                                        notifyItemRemoved(bindingAdapterPosition)

                                    }
                            }

                            builder?.setNegativeButton(binding.root.context.getString(R.string.no)) { dialog, _ ->
                                dialog.cancel()
                            }

                            builder?.show()

                        } else if (order.orderStatus == OrderStatus.Delivered) {
                            showAddReviewDialog(order.productID)
                        }
                    }else{
                        val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.NoInternetConnection), Snackbar.LENGTH_LONG)
                        val sbView: View = snackbar.view
                        sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                        snackbar.show()
                    }

                }
            }
        }

        private fun showAddReviewDialog(productId: String) {
            val binding = DialogAddReviewBinding.inflate(LayoutInflater.from(binding.root.context))
            val reviewBtnSheetDialog = BottomSheetDialog(binding.root.context)
            reviewBtnSheetDialog.setContentView(binding.root)

            FirebaseReferences.productsRef.document(productId).collection("Reviews")
                .document(UserInfo.userID!!).get().addOnSuccessListener {
                    if (it.exists()) {
                        val review: Review = it.toObject()!!
                        binding.rbAddReview.rating = review.rate
                        binding.edReview.setText(review.comment)
                        binding.btnSendReview.text =
                            binding.root.context.getString(R.string.UpdateReview)
                    }
                    reviewBtnSheetDialog.show()
                }

            binding.btnSendReview.setOnClickListener {
                val rate: Float = binding.rbAddReview.rating
                val reviewMsg = binding.edReview.text.toString()
                val review = Review(productId, UserInfo.name, UserInfo.image, reviewMsg, rate)
                FirebaseReferences.productsRef.document(productId).collection("Reviews")
                    .document(UserInfo.userID!!).set(review)
                reviewBtnSheetDialog.dismiss()
            }
        }
    }
}
