package com.alpha.shoplex.model.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ChatHeadItemRowBinding
import com.alpha.shoplex.model.pojo.ChatHead
import com.alpha.shoplex.view.activities.MessageActivity

class ChatHeadAdapter(private var chatHeads: ArrayList<ChatHead>) :
    RecyclerView.Adapter<ChatHeadAdapter.ChatHeadViewHolder>() {

    private var originalChats: ArrayList<ChatHead> = arrayListOf()

    companion object {
        const val CHAT_TITLE_KEY = "CHAT_TITLE_KEY"
        const val CHAT_IMG_KEY = "CHAT_IMG_KEY"
        const val CHAT_ID_KEY = "CHAT_ID_KEY"
        const val STORE_ID_KEY = "STORE_ID_KEY"
        const val STORE_PHONE = "STORE_PHONE"
        const val PRODUCT_ID = "PRODUCT_ID"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChatHeadViewHolder {
        return ChatHeadViewHolder(
            ChatHeadItemRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ChatHeadViewHolder, position: Int) = viewHolder.bind(chatHeads[position])

    override fun getItemCount() = chatHeads.size

    fun search(searchText: String){
        if (searchText.isNotEmpty()) {
            if (originalChats.isEmpty())
                originalChats = chatHeads
            chatHeads = originalChats.filter {
                it.productName.contains(
                    searchText,
                    true
                ) || it.storeName.contains(searchText, true)
            } as ArrayList<ChatHead>
        } else {
            chatHeads = originalChats
        }
        notifyDataSetChanged()
    }

    inner class ChatHeadViewHolder(val binding: ChatHeadItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatHead: ChatHead) {
            Glide.with(itemView.context).load(chatHead.productImageURL).error(R.drawable.init_img)
                .into(binding.imgChatHead)

            binding.chatHead = chatHead

            if (chatHead.numOfMessage > 0)
                binding.tvNumOfMessage.visibility = View.VISIBLE
            else
                binding.tvNumOfMessage.visibility = View.INVISIBLE

            if (chatHead.isStoreOnline)
                binding.cardImg.visibility = View.VISIBLE
            else
                binding.cardImg.visibility = View.INVISIBLE

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MessageActivity::class.java)
                intent.putExtra(CHAT_TITLE_KEY, chatHead.storeName)
                intent.putExtra(CHAT_IMG_KEY, chatHead.productImageURL)
                intent.putExtra(CHAT_ID_KEY, chatHead.chatId)
                intent.putExtra(PRODUCT_ID, chatHead.productID)
                intent.putExtra(STORE_ID_KEY, chatHead.storeId)
                intent.putExtra(STORE_PHONE, chatHead.storePhone)
                itemView.context.startActivity(intent)
            }
        }
    }
}