package com.alpha.shoplex.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alpha.shoplex.databinding.FragmentFavoritesBinding
import com.alpha.shoplex.model.adapter.FavouriteAdapter
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.room.viewmodel.FavouriteFactoryModel
import com.alpha.shoplex.room.viewmodel.FavouriteViewModel
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter

class FavoritesFragment : Fragment(), FavouriteCartListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favouriteViewModel: FavouriteViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        favouriteViewModel = ViewModelProvider(
            this,
            FavouriteFactoryModel(requireContext())
        ).get(FavouriteViewModel::class.java)


        getAllFavoriteProducts()

        return binding.root
    }

    private fun getAllFavoriteProducts() {
        val favouriteAdapter = FavouriteAdapter()
        binding.rvFavourite.adapter =
            ScaleInAnimationAdapter(SlideInBottomAnimationAdapter(favouriteAdapter)).apply {
                setDuration(700)
                setInterpolator(OvershootInterpolator(2f))
            }
        //binding.rvFavourite.adapter = favouriteAdapter
        favouriteViewModel.readAllFavourite.observe(viewLifecycleOwner, {
            if (it.count()>0) {
                binding.noItem.visibility=View.INVISIBLE
            }
            else{
                binding.noItem.visibility=View.VISIBLE
            }
            favouriteAdapter.setData(it)

        })
    }

    override fun onDeleteFromFavourite(productID: String) {
        favouriteViewModel.deleteFavourite(productID)
    }
}