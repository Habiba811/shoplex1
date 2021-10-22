package com.alpha.shoplex.view.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FragmentHomeBinding
import com.alpha.shoplex.model.adapter.AdvertisementsAdapter
import com.alpha.shoplex.model.adapter.HomeAdapter
import com.alpha.shoplex.model.enumurations.Category
import com.alpha.shoplex.model.enumurations.LocationAction
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.pojo.*
import com.alpha.shoplex.room.viewmodel.CartViewModel
import com.alpha.shoplex.view.activities.FilterActivity
import com.alpha.shoplex.view.activities.MapsActivity
import com.alpha.shoplex.viewmodel.ProductsVM
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter


class HomeFragment : Fragment(), FavouriteCartListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var startActivityLaunch: ActivityResultLauncher<Intent>
    private lateinit var advertisementsAdapter: AdvertisementsAdapter
    private lateinit var homeProductAdapter: HomeAdapter
    private lateinit var productsVM: ProductsVM
    private var selectedCategory: String = Category.Fashion.name
    private lateinit var cartVM: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    if (data != null)
                        startFilter(data)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnFilter.setOnClickListener {

            startActivityLaunch.launch(Intent(context, FilterActivity::class.java).apply {
                this.putExtra(FilterActivity.FILTER, productsVM.filter.value)
                this.putExtra(FilterActivity.SORT, productsVM.sort.value)
                this.putExtra(FilterActivity.SELECTED_ITEM, selectedCategory)
            })
        }

        this.productsVM = ProductsVM()
        for ((index, _) in productsVM.getCategories().withIndex()) {
            val chip = inflater.inflate(R.layout.chip_choice_item, null, false) as Chip
            chip.text = resources.getStringArray(R.array.categories)[index]
            chip.id = index
            chip.chipIcon = when (index) {
                0 -> ContextCompat.getDrawable(requireActivity(), R.drawable.fashion_chip)
                1 -> ContextCompat.getDrawable(requireActivity(), R.drawable.beauty)
                2 -> ContextCompat.getDrawable(requireActivity(), R.drawable.smartphone)
                3 -> ContextCompat.getDrawable(requireActivity(), R.drawable.electroincs)
                4 -> ContextCompat.getDrawable(requireActivity(), R.drawable.accessories)
                else -> ContextCompat.getDrawable(requireActivity(), R.drawable.book)
            }
            binding.chipGroup.addView(chip)
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedCategory = group.findViewById<Chip>(checkedId).text.toString()
            val category =
                Category.values()[checkedId].name.replace("_", " ")

            productsVM.getAllProducts(category, Filter(), null)
        }

        binding.chipGroup.findViewById<Chip>(binding.chipGroup.children.first().id).isChecked = true

        productsVM.getAllPremiums()
        productsVM.advertisements.observe(viewLifecycleOwner, { advertisements ->
            advertisementsAdapter = AdvertisementsAdapter(advertisements)
            binding.rvAdvertisement.adapter =
                ScaleInAnimationAdapter(SlideInLeftAnimationAdapter(advertisementsAdapter)).apply {
                    setDuration(1000)
                    setInterpolator(OvershootInterpolator(2f))
                    setFirstOnly(false)
                }
            //binding.rvAdvertisement.adapter = advertisementsAdapter
        })

        // Products
        cartVM = ViewModelProvider(this).get(CartViewModel::class.java)
        binding.rvHomeproducts.layoutManager =
            GridLayoutManager(requireActivity(), getGridColumnsCount())

        productsVM.products.observe(viewLifecycleOwner, { products ->
            homeProductAdapter = HomeAdapter(products)
            binding.rvHomeproducts.adapter =
                ScaleInAnimationAdapter(SlideInBottomAnimationAdapter(homeProductAdapter)).apply {
                    setDuration(1000)
                    setInterpolator(OvershootInterpolator(2f))
                    setFirstOnly(false)
                }
            // binding.rvHomeproducts.adapter = homeProductAdapter
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty() && this@HomeFragment::homeProductAdapter.isInitialized) {
                    homeProductAdapter.search("")
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (this@HomeFragment::homeProductAdapter.isInitialized)
                    homeProductAdapter.search(binding.searchView.query.toString())
                return false
            }
        })

        binding.btnLocation.setOnClickListener {
            if (ArchLifecycleApp.isInternetConnected) {
                val addresses = arrayListOf<String>()
                val locations = ArrayList<LatLng>()

                homeProductAdapter.productsHome.groupBy {
                    it.storeLocation
                }.forEach {
                    val product = it.value.first()
                    addresses.add(product.storeName)
                    locations.add(LatLng(it.key.latitude, it.key.longitude))
                }

                if(locations.count() == 0) {
                    Toast.makeText(requireContext(), "No stores for this category!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                startActivity(
                    Intent(requireContext(), MapsActivity::class.java)
                        .apply {
                            putExtra(MapsActivity.LOCATION_ACTION, LocationAction.ShowStores.name)


                            putParcelableArrayListExtra(MapsActivity.STORE_LOCATIONS, locations)
                            putStringArrayListExtra(MapsActivity.STORE_ADDRESSES, addresses)
                        }
                )
            } else {
                val snackbar = Snackbar.make(
                    binding.root,
                    binding.root.context.getString(R.string.NoInternetConnection),
                    Snackbar.LENGTH_LONG
                )
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.blueshop
                    )
                )
                snackbar.show()
            }
        }

        return binding.root
    }

    private fun getGridColumnsCount(): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val scalingFactor = 200
        val columnCount = (dpWidth / scalingFactor).toInt()
        return if (columnCount >= 2) columnCount else 2
    }

    private fun startFilter(data: Intent) {
        val filter: Parcelable? = data.getParcelableExtra(FilterActivity.FILTER)
        val sort: Parcelable? = data.getParcelableExtra(FilterActivity.SORT)

        val userFilter: Filter = filter as Filter
        val userSort = sort as? Sort
        val selectedIndex =
            requireContext().resources.getStringArray(R.array.categories).indexOf(selectedCategory)
        val category = Category.values()[selectedIndex].name.replace("_", " ")

        productsVM.getAllProducts(category, userFilter, userSort)
    }

    override fun onAddToCart(productCart: ProductCart) {
        cartVM.addCart(productCart)
    }
}