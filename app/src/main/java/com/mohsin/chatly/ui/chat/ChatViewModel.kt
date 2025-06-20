package com.mohsin.chatly.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohsin.chatly.data.model.Message
import com.mohsin.chatly.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    private var chatUserId: String? = null
    private var myUserId: String? = null

    fun setChatUserIds(chatWithUserId: String, currentUserId: String) {
        chatUserId = chatWithUserId
        myUserId = currentUserId
    }

    // Use Room Flow for real-time updates
    val messages: Flow<List<Message>>?
        get() = if (chatUserId != null && myUserId != null)
            repository.getMessagesWithUser(chatUserId!!, myUserId!!)
        else null

    // Insert into Room AND remote on send
    fun sendMessage(msg: Message) {
        viewModelScope.launch {
            repository.sendMessage(msg) // inserts to Room and sends to Firestore
        }
    }

    fun sync() {
        if (chatUserId != null && myUserId != null) {
            viewModelScope.launch {
                repository.syncMessages(chatUserId!!, myUserId!!)
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }
}