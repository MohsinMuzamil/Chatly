package com.mohsin.chatly.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mohsin.chatly.R
import com.mohsin.chatly.data.model.Message
import com.mohsin.chatly.data.repository.ChatRepository
import com.mohsin.chatly.data.remote.FirebaseService
import com.mohsin.chatly.ui.components.TypingIndicatorView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mohsin.chatly.data.local.AppDatabase
import com.bumptech.glide.Glide
import java.util.UUID
import com.google.android.material.imageview.ShapeableImageView
import com.mohsin.chatly.ui.chat.UserDetailActivity

class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var typingIndicator: TypingIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val ivUserAvatar = findViewById<ShapeableImageView>(R.id.ivUserAvatar)
        val rvMessages: RecyclerView = findViewById(R.id.rvMessages)
        val etMessage: EditText = findViewById(R.id.etMessage)
        val btnSend: Button = findViewById(R.id.btnSend)
        typingIndicator = findViewById(R.id.typingIndicator)

        // Defensive: make sure intent and required extras are present
        val myIntent = intent ?: run {
            finish()
            return
        }
        val chatUserId = myIntent.getStringExtra("userId") ?: run {
            finish()
            return
        }
        val chatUserName = myIntent.getStringExtra("userName") ?: ""
        val chatUserPhotoUrl = myIntent.getStringExtra("userPhotoUrl")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            finish()
            return
        }

        // Instantiate dependencies
        val messageDao = AppDatabase.getInstance(this).messageDao()
        val firebaseService = FirebaseService()
        val repository = ChatRepository(messageDao, firebaseService)
        val factory = ChatViewModelFactory(repository)
        val tvNoMessages = findViewById<TextView>(R.id.tvNoMessages)
        viewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        tvUserName.text = chatUserName
        viewModel.setChatUserIds(chatUserId, currentUserId)

        // Load user photo if available
        if (!chatUserPhotoUrl.isNullOrBlank()) {
            Glide.with(this).load(chatUserPhotoUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(ivUserAvatar)
        } else {
            ivUserAvatar.setImageResource(R.drawable.ic_profile_placeholder)
        }

        adapter = ChatAdapter(emptyList(), currentUserId) { message ->
            if (message.messageId.isNullOrBlank()) {
                Toast.makeText(this, "Cannot delete: messageId is blank!", Toast.LENGTH_SHORT).show()
                return@ChatAdapter
            }
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteMessage(message.messageId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        viewModel.messages?.let { msgFlow ->
            lifecycleScope.launch {
                msgFlow.collectLatest { messages ->
                    adapter.submitList(messages)
                    if (messages.isEmpty()) {
                        tvNoMessages.visibility = View.VISIBLE
                    } else {
                        tvNoMessages.visibility = View.GONE
                        rvMessages.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }

        ivUserAvatar.setOnClickListener {
            openUserDetail(chatUserId, chatUserName, chatUserPhotoUrl)
        }
        tvUserName.setOnClickListener {
            openUserDetail(chatUserId, chatUserName, chatUserPhotoUrl)
        }

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                val msg = Message(
                    messageId = UUID.randomUUID().toString(),
                    senderId = currentUserId,
                    receiverId = chatUserId,
                    content = content,
                    timestamp = System.currentTimeMillis(),
                    isMine = true
                )
                viewModel.sendMessage(msg)
                etMessage.text.clear()
            }
        }
    }

    /**
     * Launches the UserDetailActivity to show the user's details.
     */
    private fun openUserDetail(
        userId: String,
        userName: String,
        userPhotoUrl: String?
    ) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("userPhotoUrl", userPhotoUrl)
        startActivity(intent)
    }
}