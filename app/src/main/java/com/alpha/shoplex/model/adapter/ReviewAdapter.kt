package com.alpha.shoplex.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ReveiwItemBinding
import com.alpha.shoplex.model.pojo.Review

class ReviewAdapter(val reviews: ArrayList<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ReveiwItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) =
        holder.bind(reviews[position])

    override fun getItemCount() = reviews.size

    inner class ReviewViewHolder(val binding: ReveiwItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.review = review
            Glide.with(itemView.context).load(review.image).error(R.drawable.init_img).into(binding.imgHead)
            binding.ratingBar.rating = review.rate
        }
    }
}
