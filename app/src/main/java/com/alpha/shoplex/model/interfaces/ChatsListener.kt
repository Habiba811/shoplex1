package com.alpha.shoplex.model.interfaces

import com.alpha.shoplex.model.pojo.ChatHead

interface ChatsListener {
    fun onChatHeadsReady(chatHeads: ArrayList<ChatHead>){}
    fun onChatHeadChanged(position: Int){}
}