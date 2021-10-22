package com.alpha.shoplex.view.fragments

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.FragmentDetailsBinding
import com.alpha.shoplex.model.adapter.ChatHeadAdapter
import com.alpha.shoplex.model.adapter.PropertyAdapter
import com.alpha.shoplex.model.extra.ArchLifecycleApp
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.interfaces.FavouriteCartListener
import com.alpha.shoplex.model.maps.LocationManager
import com.alpha.shoplex.model.pojo.*
import com.alpha.shoplex.room.data.ShopLexDataBase
import com.alpha.shoplex.room.repository.FavoriteCartRepo
import com.alpha.shoplex.view.activities.CheckOutActivity
import com.alpha.shoplex.view.activities.DetailsActivity
import com.alpha.shoplex.view.activities.MessageActivity
import com.alpha.shoplex.viewmodel.DetailsVM
import com.alpha.shoplex.viewmodel.ProductsVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DetailsFragment : Fragment(), FavouriteCartListener {
    private lateinit var binding: FragmentDetailsBinding
    private var product: Product = Product()
    private val imageList = ArrayList<SlideModel>()

    private lateinit var productsVM: ProductsVM
    private lateinit var detailsVM: DetailsVM

    private lateinit var repo: FavoriteCartRepo
    private lateinit var lifecycleScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        lifecycleScope = requireActivity().lifecycleScope
        repo = FavoriteCartRepo(ShopLexDataBase.getDatabase(binding.root.context).shopLexDao())

        productsVM = (requireActivity() as DetailsActivity).productsVM
        detailsVM = (requireActivity() as DetailsActivity).detailsVM

        if (productsVM.products.value == null)
            productsVM.getProduct()
        else if (productsVM.products.value!!.isNotEmpty())
            product = productsVM.products.value!!.first()

        binding.tvDetailsNewPrice.paintFlags = binding.tvDetailsNewPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        productsVM.products.observe(this.viewLifecycleOwner, { products ->
            if (products.isNotEmpty()) {
                this.product = products.first()
                detailsVM.getStoreData(product.storeID)
                binding.product = product
                imageList.clear()
                for (img in product.images)
                    imageList.add(SlideModel(img))
                if (product.images.isEmpty())
                    imageList.add(SlideModel(R.drawable.init_img))
                binding.imgSliderDetails.setImageList(imageList, ScaleTypes.CENTER_CROP)
                binding.rvProperty.adapter = PropertyAdapter(product.properties, requireContext())
                onSearchForFavouriteCart(product.productID)
                if (product.quantity == 0) {
                    binding.linearLayout.isEnabled = false
                    binding.linearLayout.visibility = View.INVISIBLE
                    onDeleteFromCart(product.productID)
                }

                if(product.quantity - product.sold <= 0){
                    binding.btnFavourite.visibility = View.INVISIBLE
                    binding.btnAddToCart.visibility = View.INVISIBLE
                    binding.btnBuyProduct.visibility = View.INVISIBLE
                }

                if(product.price == product.newPrice){
                    binding.tvDetailsNewPrice.visibility = View.INVISIBLE
                }
                productsVM.products.removeObservers(this)
            }
        })

        repo.searchFavouriteByID.observe(context as AppCompatActivity, {
            if (it == null) {
                binding.btnFavourite.setBackgroundResource(R.drawable.ic_favorite)
                product.isFavourite = false
            } else {
                binding.btnFavourite.setBackgroundResource(R.drawable.ic_favorite_fill)
                product.isFavourite = true
            }
        })

        repo.searchCartByID.observe(context as AppCompatActivity, {
            if (it == null) {
                binding.btnAddToCart.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_cart),
                    null
                )
                product.isCart = false
                binding.btnAddToCart.text = getString(R.string.Add)
            } else {
                binding.btnAddToCart.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_done),
                    null
                )
                product.isCart = true
                binding.btnAddToCart.text = getString(R.string.Remove)
            }
        })

        binding.btnCall.setOnClickListener {
            if(detailsVM.store.value != null) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data =
                    Uri.parse(getString(R.string.telephone) + detailsVM.store.value!!.phone)
                startActivity(intent)
            } else {
                val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.Telephone), Snackbar.LENGTH_LONG)
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                snackbar.show()
            }
        }

        binding.btnFavourite.setOnClickListener {
            if (product.isFavourite) {
                onDeleteFromFavourite(product.productID)
                binding.btnFavourite.setBackgroundResource(R.drawable.ic_favorite)
                product.isFavourite = false
            } else {
                onAddToFavourite(ProductFavourite(product))
                binding.btnFavourite.setBackgroundResource(R.drawable.ic_favorite_fill)
                product.isFavourite = true
            }
        }

        binding.btnMessage.setOnClickListener {
            if(!ArchLifecycleApp.isInternetConnected) {
                val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.NoInternetConnection), Snackbar.LENGTH_LONG)
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                snackbar.show()
                return@setOnClickListener
            }
            FirebaseReferences.chatRef.whereEqualTo("storeID", product.storeID)
                .whereEqualTo("userID", UserInfo.userID).get().addOnSuccessListener { values ->
                    when {
                        values.count() == 0 -> {
                            val newChatRef = FirebaseReferences.chatRef.document()
                            val chat = Chat(
                                newChatRef.id,
                                UserInfo.userID!!,
                                product.storeID,
                                UserInfo.name,
                                product.storeName,
                                detailsVM.store.value!!.phone,
                                true,
                                productIDs = arrayListOf(productsVM.productID.value!!),
                                storeImage = detailsVM.store.value!!.image
                            )
                            newChatRef.set(chat).addOnSuccessListener {
                                initMessage(chat.chatID)
                                openMessagesActivity(chat.chatID)
                            }
                        }
                        values.count() == 1 -> {
                            val chat: Chat = values.first().toObject()
                            if (chat.productIDs.last() != productsVM.productID.value!!)
                                initMessage(chat.chatID)
                            FirebaseReferences.chatRef.document(chat.chatID)
                                .update(
                                    "productIDs",
                                    FieldValue.arrayUnion(productsVM.productID.value!!)
                                )
                            openMessagesActivity(chat.chatID)
                        }
                        else -> {
                            val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.ERROR), Snackbar.LENGTH_LONG)
                            val sbView: View = snackbar.view
                            sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                            snackbar.show()

                        }
                    }

                }
        }

        binding.imgLocation.setOnClickListener {
            LocationManager.getInstance(requireContext()).launchGoogleMaps(product.storeLocation)
        }

        binding.btnBuyProduct.setOnClickListener {
            if (UserInfo.userID != null) {
                if (ArchLifecycleApp.isInternetConnected) {
                    val selectedProperties: ArrayList<String> = arrayListOf()
                    for (property in product.properties) {
                        if (property.selectedProperty != null)
                            selectedProperties.add(property.selectedProperty!!)
                    }

                    startActivity(Intent(context, CheckOutActivity::class.java).apply {
                        this.putParcelableArrayListExtra(
                            CheckOutActivity.PRODUCTS_QUANTITY,
                            arrayListOf<ProductQuantity>().apply {
                                this.add(ProductQuantity(product.productID, 1))
                            })

                        if (selectedProperties.isNotEmpty())
                            this.putStringArrayListExtra(
                                CheckOutActivity.PRODUCT_PROPERTIES,
                                selectedProperties
                            )

                        this.putExtra("isBuyNow", true)

                    })
                }else {
                    val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.NoInternetConnection), Snackbar.LENGTH_LONG)
                    val sbView: View = snackbar.view
                    sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                    snackbar.show()
                }


            } else {
                val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.pleaseLogin), Snackbar.LENGTH_LONG)
                val sbView: View = snackbar.view
                sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                snackbar.show()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            if (product.isCart) {
                onDeleteFromCart(product.productID)
                binding.btnAddToCart.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_cart),
                    null
                )
                product.isCart = false
                binding.btnAddToCart.text = getString(R.string.Add)
            } else {
                onAddToCart(ProductCart(product = product))
                binding.btnAddToCart.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_done),
                    null
                )
                product.isCart = true
                binding.btnAddToCart.text = getString(R.string.Remove)
            }
        }

        return binding.root
    }

    private fun openMessagesActivity(chatID: String) {
        binding.root.context.startActivity(
            Intent(
                binding.root.context,
                MessageActivity::class.java
            ).apply {
                this.putExtra(ChatHeadAdapter.CHAT_TITLE_KEY, product.storeName)
                this.putExtra(ChatHeadAdapter.CHAT_IMG_KEY, product.images.firstOrNull())
                this.putExtra(ChatHeadAdapter.CHAT_ID_KEY, chatID)
                this.putExtra(ChatHeadAdapter.PRODUCT_ID, product.productID)
                this.putExtra(ChatHeadAdapter.STORE_ID_KEY, product.storeID)
                this.putExtra(ChatHeadAdapter.STORE_PHONE, detailsVM.store.value!!.phone)
            })
    }

    private fun initMessage(chatID: String) {
        val message = Message(
            toId = UserInfo.userID!!, message = "You started new chat for " + product.name
        )
        FirebaseReferences.chatRef.document(chatID).collection("messages")
            .document(message.messageID)
            .set(message)
    }

    override fun onAddToCart(productCart: ProductCart) {
        super.onAddToCart(productCart)
        lifecycleScope.launch {
            productCart.cartQuantity = 1
            repo.addCart(productCart)
        }
    }

    override fun onDeleteFromCart(productID: String) {
        super.onDeleteFromCart(productID)
        lifecycleScope.launch {
            repo.deleteCart(productID)
        }
    }

    override fun onAddToFavourite(productFavourite: ProductFavourite) {
        super.onAddToFavourite(productFavourite)
        lifecycleScope.launch {
            repo.addFavourite(productFavourite)
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