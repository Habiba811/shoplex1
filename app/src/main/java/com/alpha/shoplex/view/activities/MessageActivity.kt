package com.alpha.shoplex.view.activities

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.toObject
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityMessageBinding
import com.alpha.shoplex.model.adapter.ChatHeadAdapter
import com.alpha.shoplex.model.adapter.LeftMessageItem
import com.alpha.shoplex.model.adapter.RightMessageItem
import com.alpha.shoplex.model.extra.FirebaseReferences
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.pojo.Message
import com.alpha.shoplex.room.viewmodel.MessageFactoryModel
import com.alpha.shoplex.room.viewmodel.MessageViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dialog_add_report.view.*


class MessageActivity : AppCompatActivity(), DroidListener {
    private lateinit var binding: ActivityMessageBinding
    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var chatID: String
    private lateinit var storeID: String
    private lateinit var productID: String
    private lateinit var phoneNumber: String
    private var position: Int = -1
    private lateinit var messageVM: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMessage)
        supportActionBar?.title = ""

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        DroidNet.getInstance().addInternetConnectivityListener(this)

        val userName = intent.getStringExtra(ChatHeadAdapter.CHAT_TITLE_KEY)
        val productImg = intent.getStringExtra(ChatHeadAdapter.CHAT_IMG_KEY).toString()
        chatID = intent.getStringExtra(ChatHeadAdapter.CHAT_ID_KEY).toString()
        productID = intent.getStringExtra(ChatHeadAdapter.PRODUCT_ID).toString()
        storeID = intent.getStringExtra(ChatHeadAdapter.STORE_ID_KEY).toString()
        phoneNumber = intent.getStringExtra(ChatHeadAdapter.STORE_PHONE).toString()

        messageVM = ViewModelProvider(
            this,
            MessageFactoryModel(this, chatID)
        ).get(MessageViewModel::class.java)

        messageVM.isOnline.observe(this, {
            if (it) {
                binding.cardIsOnline.visibility = View.VISIBLE
            } else {
                binding.cardIsOnline.visibility = View.INVISIBLE
            }
        })

       // binding.imgToolbarback.setOnClickListener { finish() }

        binding.imgToolbarChat.setImageResource(R.drawable.placeholder)
        binding.tvToolbarUserChat.text = userName
        Glide.with(this).load(productImg).into(binding.imgToolbarChat)

        getAllMessage()

        binding.btnSendMessage.setOnClickListener {
            performSendMessage()
        }
    }

    private fun performSendMessage() {
        //send Message to Firebase
        val messageText = binding.edSendMesssage.text
        if(messageText.trim().isEmpty())
            return
        val message = Message(toId = storeID, message = messageText.toString())
        message.chatID = chatID
        FirebaseReferences.chatRef.document(chatID).collection("messages")
            .document(message.messageID)
            .set(message).addOnSuccessListener {
                messageAdapter.add(RightMessageItem(message, messageVM))
                messageText.clear()
                binding.rvMessage.smoothScrollToPosition(messageAdapter.groupCount)
            }
    }

    private fun listenToNewMessages(lastID: String) {
        var firstTimeToLoadMessages = (lastID == "1")
        FirebaseReferences.chatRef.document(chatID).collection("messages")
            .whereGreaterThan("messageID", lastID).addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                for ((index, dc) in snapshots!!.documentChanges.withIndex()) {
                    if ((dc.type) == DocumentChange.Type.ADDED) {
                        val message = dc.document.toObject<Message>()
                        if (message.toId == UserInfo.userID) {
                            message.chatID = chatID
                            if (!message.isSent) {
                                FirebaseReferences.chatRef.document(chatID).collection("messages")
                                    .document(message.messageID).update("isSent", true)
                                message.isSent = true
                            }

                            if (!message.isRead && position == -1)
                                position = messageAdapter.groupCount + index - 1

                            messageAdapter.add(LeftMessageItem(chatID, message, messageVM))
                            messageVM.addMessage(message)
                        } else if (message.toId != UserInfo.userID) {
                            if (firstTimeToLoadMessages)
                                messageAdapter.add(RightMessageItem(message, messageVM))
                            message.chatID = chatID
                            messageVM.addMessage(message)
                        }
                    }
                }

                firstTimeToLoadMessages = false

                if (position > 0) {
                    binding.rvMessage.smoothScrollToPosition(position)
                    position = 0
                }
            }
    }

    private fun getAllMessage() {
        binding.rvMessage.adapter = messageAdapter
        messageVM.readAllMessage.observe(this, {
            for (message in it) {
                if (message.toId == UserInfo.userID) {
                    messageAdapter.add(LeftMessageItem(chatID, message, messageVM))

                } else if (message.toId != UserInfo.userID) {
                    messageAdapter.add(RightMessageItem(message, messageVM))
                }
            }
            val lastID = if (it.isEmpty()) {
                "1"
            } else {
                binding.rvMessage.scrollToPosition(it.count() - 1)
                it.last().messageID
            }
            messageVM.readAllMessage.removeObservers(this)
            listenToNewMessages(lastID)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        val drawable = menu!!.getItem(0).icon
        drawable.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.call -> {
                if (phoneNumber.length > 10) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse(getString(R.string.tel) + phoneNumber)
                    startActivity(intent)
                } else {
                    val snackbar = Snackbar.make(binding.root, binding.root.context.getString(R.string.phone_not_specified), Snackbar.LENGTH_LONG)
                    val sbView: View = snackbar.view
                    sbView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blueshop))
                    snackbar.show()
                }
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            binding.spinKitMsg.visibility = View.INVISIBLE
            binding.tvLoad.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.spinKitMsg.visibility = View.VISIBLE
            binding.tvLoad.visibility = View.VISIBLE
        }
    }


}