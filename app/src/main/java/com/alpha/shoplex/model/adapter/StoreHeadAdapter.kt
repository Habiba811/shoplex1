package com.alpha.shoplex.model.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.StoreItemRowBinding
import com.alpha.shoplex.model.pojo.ChatHead
import com.alpha.shoplex.view.activities.MessageActivity

class StoreHeadAdapter(private var storeHeads: ArrayList<ChatHead>) :
    RecyclerView.Adapter<StoreHeadAdapter.StoreHeadViewHolder>() {

    private var originalChats: ArrayList<ChatHead> = arrayListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StoreHeadViewHolder {
        return StoreHeadViewHolder(
            StoreItemRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: StoreHeadViewHolder, position: Int) {
        viewHolder.bind(storeHeads[position])
    }

    override fun getItemCount() = storeHeads.size

    fun search(searchText: String){
        if (searchText.isNotEmpty()) {
            if (originalChats.isEmpty())
                originalChats = storeHeads
            storeHeads = originalChats.filter {
                it.productName.contains(
                    searchText,
                    true
                ) || it.storeName.contains(searchText, true)
            } as ArrayList<ChatHead>
        } else {
            storeHeads = originalChats
        }
        notifyDataSetChanged()
    }

    inner class StoreHeadViewHolder(val binding: StoreItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storeHead: ChatHead) {
            Glide.with(itemView.context).load(storeHead.storeImage).error(R.drawable.init_img).into(binding.imgStoreHead)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MessageActivity::class.java)
                intent.putExtra(ChatHeadAdapter.CHAT_TITLE_KEY, storeHead.storeName)
                intent.putExtra(ChatHeadAdapter.CHAT_IMG_KEY, storeHead.productImageURL)
                intent.putExtra(ChatHeadAdapter.CHAT_ID_KEY, storeHead.chatId)
                intent.putExtra(ChatHeadAdapter.PRODUCT_ID, storeHead.productID)
                intent.putExtra(ChatHeadAdapter.STORE_ID_KEY, storeHead.storeId)
                intent.putExtra(ChatHeadAdapter.STORE_PHONE, storeHead.storePhone)
                itemView.context.startActivity(intent)
            }
        }
    }
}