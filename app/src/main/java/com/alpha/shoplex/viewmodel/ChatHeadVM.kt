package com.alpha.shoplex.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alpha.shoplex.model.firebase.ChatHeadDBModel
import com.alpha.shoplex.model.interfaces.ChatsListener
import com.alpha.shoplex.model.pojo.ChatHead

class ChatHeadVM: ViewModel(), ChatsListener {

    private val chatHeadDBModel = ChatHeadDBModel(this)
    var chatHeads: MutableLiveData<ArrayList<ChatHead>> = MutableLiveData()
    var changedPosition: MutableLiveData<Int> = MutableLiveData()

    fun getChatHeads(){
        chatHeadDBModel.getChatHeads()
    }

    override fun onChatHeadsReady(chatHeads: ArrayList<ChatHead>) {
        this.chatHeads.value = chatHeads
    }

    override fun onChatHeadChanged(position: Int) {
        changedPosition.value = position
    }
}