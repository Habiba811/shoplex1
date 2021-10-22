package com.alpha.shoplex.model.adapter

import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ChatItemRightBinding
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.pojo.Message
import com.alpha.shoplex.room.viewmodel.MessageViewModel
import com.xwray.groupie.databinding.BindableItem

class RightMessageItem(val message: Message, private val messageVM: MessageViewModel) : BindableItem<ChatItemRightBinding>() {

    override fun bind(binding: ChatItemRightBinding, position: Int) {
        binding.message = message

        if (!message.isRead) {
            FirebaseReferences.chatRef.document(message.chatID).collection("messages")
                .whereEqualTo("messageID", message.messageID).addSnapshotListener { value, error ->
                    if (value == null || error != null || value.documents.isNullOrEmpty())
                        return@addSnapshotListener

                    val updatedMessage = value.documents.first().toObject<Message>()
                    if (updatedMessage != null) {
                        updatedMessage.chatID = message.chatID
                        if (updatedMessage.isSent)
                            messageVM.setSent(updatedMessage.messageID)
                        if (updatedMessage.isRead)
                            messageVM.setRead(updatedMessage.messageID)
                        binding.message = updatedMessage
                    }
                }
        }
    }

    override fun getLayout(): Int = R.layout.chat_item_right
}