package com.mohsin.chatly.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mohsin.chatly.data.model.Message
import kotlinx.coroutines.tasks.await

class FirebaseService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {


    suspend fun sendMessage(message: Message) {
        if (message.messageId.isNotBlank()) {
            firestore.collection("messages")
                .document(message.messageId)
                .set(message)
                .await()
        } else {
            // Auto-generate ID if missing
            firestore.collection("messages")
                .add(message)
                .await()
        }
    }

    // Fetch all messages between current user (myUserId) and chat partner (chatUserId)
    suspend fun fetchMessagesWithUser(chatUserId: String, myUserId: String): List<Message> {
        val sentSnapshot = firestore.collection("messages")
            .whereEqualTo("senderId", myUserId)
            .whereEqualTo("receiverId", chatUserId)
            .orderBy("timestamp")
            .get()
            .await()
        val sentMessages = sentSnapshot.toObjects(Message::class.java)

        val receivedSnapshot = firestore.collection("messages")
            .whereEqualTo("senderId", chatUserId)
            .whereEqualTo("receiverId", myUserId)
            .orderBy("timestamp")
            .get()
            .await()
        val receivedMessages = receivedSnapshot.toObjects(Message::class.java)

        val allMessages = (sentMessages + receivedMessages).sortedBy { it.timestamp }
        return allMessages.map { it.copy(isMine = it.senderId == myUserId) }
    }

    suspend fun deleteMessage(messageId: String) {
        if (messageId.isBlank()) return
        firestore.collection("messages")
            .document(messageId)
            .delete()
            .await()
    }

    fun listenForMessages(chatUserId: String, myUserId: String, onMessage: (Message) -> Unit) {
        firestore.collection("messages")
            .whereIn("senderId", listOf(myUserId, chatUserId))
            .whereIn("receiverId", listOf(myUserId, chatUserId))
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.toObjects(Message::class.java)
                messages.forEach { onMessage(it) }
            }
    }

}