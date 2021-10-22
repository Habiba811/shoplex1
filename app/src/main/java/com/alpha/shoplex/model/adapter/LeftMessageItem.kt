package com.alpha.shoplex.model.adapter

import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ChatItemLeftBinding
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.pojo.Message
import com.alpha.shoplex.room.viewmodel.MessageViewModel
import com.xwray.groupie.databinding.BindableItem

class LeftMessageItem(private val chatID: String, val message: Message, private val messageVM: MessageViewModel) : BindableItem<ChatItemLeftBinding>() {

    override fun bind(binding: ChatItemLeftBinding, position: Int) {
        binding.message = message
        if (!message.isRead) {
            FirebaseReferences.chatRef.document(chatID).collection("messages")
                .document(message.messageID).update("isRead", true)
            messageVM.setRead(message.messageID)
        }
    }

    override fun getLayout(): Int = R.layout.chat_item_left
}