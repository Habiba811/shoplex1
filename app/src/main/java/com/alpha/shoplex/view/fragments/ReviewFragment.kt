package com.alpha.shoplex.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alpha.shoplex.databinding.FragmentReviewBinding
import com.alpha.shoplex.model.adapter.ReviewAdapter
import com.alpha.shoplex.view.activities.DetailsActivity
import com.alpha.shoplex.viewmodel.ProductsVM

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var productsVM: ProductsVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentReviewBinding.inflate(inflater, container, false)
        productsVM = (requireActivity() as DetailsActivity).productsVM

        if(productsVM.reviews.value == null)
            productsVM.getReviews()

        productsVM.reviews.observe(viewLifecycleOwner, { reviews ->
            reviewAdapter = ReviewAdapter(reviews)
            binding.rvReview.adapter = reviewAdapter
        })

        productsVM.reviewStatistics.observe(viewLifecycleOwner, {
            binding.reviewStat = it
            if (it.total != 0) {
                binding.fiveStars.progress = ((it.fiveStars.toFloat() / it.total) * 100).toInt()
                binding.fourStars.progress = ((it.fourStars.toFloat() / it.total) * 100).toInt()
                binding.threeStars.progress = ((it.threeStars.toFloat() / it.total) * 100).toInt()
                binding.twoStars.progress = ((it.twoStars.toFloat() / it.total) * 100).toInt()
                binding.oneStar.progress = ((it.oneStar.toFloat() / it.total) * 100).toInt()
            }
        })

        return binding.root
    }
}