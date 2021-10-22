package com.alpha.shoplex.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpha.shoplex.databinding.ShopItemBinding
import com.alpha.shoplex.model.pojo.StoreLocationInfo

class StoresLocationsAdapter(
    val stores: ArrayList<StoreLocationInfo>,
    val storesList: ArrayList<String>
) :
    RecyclerView.Adapter<StoresLocationsAdapter.StoreLocationInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreLocationInfoViewHolder {
        return StoreLocationInfoViewHolder(
            ShopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoreLocationInfoViewHolder, position: Int) =
        holder.bind(stores[position])

    override fun getItemCount() = stores.size

    inner class StoreLocationInfoViewHolder(val binding: ShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(locationInfo: StoreLocationInfo) {
            binding.locationInfo = locationInfo

            itemView.setOnClickListener {
                binding.cbShopName.isChecked = !binding.cbShopName.isChecked
                if (binding.cbShopName.isChecked)
                    storesList.add(locationInfo.storeID)
                else
                    storesList.remove(locationInfo.storeID)
            }

            binding.cbShopName.setOnClickListener {
                if (binding.cbShopName.isChecked)
                    storesList.add(locationInfo.storeID)
                else
                    storesList.remove(locationInfo.storeID)
            }

            if (storesList.contains(locationInfo.storeID))
                binding.cbShopName.isChecked = true
        }
    }
}