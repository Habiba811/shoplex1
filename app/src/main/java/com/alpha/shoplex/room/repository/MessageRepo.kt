package com.alpha.shoplex.room.repository

import androidx.lifecycle.LiveData
import com.alpha.shoplex.model.pojo.Message
import com.alpha.shoplex.room.data.ShopLexDao

class MessageRepo(private val messageDao: ShopLexDao, val chatID: String) {

    val readAllMessage: LiveData<List<Message>> = messageDao.readAllMessage(chatID)

    suspend fun addMessage(rightMessage: Message) {
        messageDao.addMessage(rightMessage)
    }

    fun setSent(messageID: String) {
        messageDao.setSent(messageID)
    }

    fun setReadMessage(messageID: String) {
        messageDao.setReadMessage(messageID)
    }
}