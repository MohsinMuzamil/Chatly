package com.mohsin.chatly.data.repository

import com.mohsin.chatly.data.local.MessageDao
import com.mohsin.chatly.data.model.Message
import com.mohsin.chatly.data.remote.FirebaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ChatRepository(
    private val messageDao: MessageDao,
    private val firebaseService: FirebaseService
) {
    // Get all messages with a specific user (for one-to-one chat)
    fun getMessagesWithUser(chatUserId: String, myUserId: String): Flow<List<Message>> =
        messageDao.getMessagesWithUser(chatUserId, myUserId)

    // Send a message to a specific user
    suspend fun sendMessage(message: Message) {
        firebaseService.sendMessage(message)
        messageDao.insertMessage(message)
    }

    // Synchronize messages with a user
    suspend fun syncMessages(chatUserId: String, myUserId: String) {
        val remoteMessages = firebaseService.fetchMessagesWithUser(chatUserId, myUserId)
        messageDao.insertMessages(remoteMessages)
    }

    suspend fun deleteMessage(messageId: String) {
        firebaseService.deleteMessage(messageId)
        messageDao.deleteMessageById(messageId)
    }


    // Offline-first: load local, then sync in background
    fun getMessagesOfflineFirst(chatUserId: String, myUserId: String): Flow<List<Message>> = flow {
        emit(messageDao.getMessagesWithUser(chatUserId, myUserId).first()) // emit local cache
        try {
            val remote = firebaseService.fetchMessagesWithUser(chatUserId, myUserId)
            messageDao.insertMessages(remote)
            emit(messageDao.getMessagesWithUser(chatUserId, myUserId).first()) // emit updated
        } catch (e: Exception) {
            // If offline, just keep local
        }
    }

    fun sendMessageToRemote(msg: Message) {

    }
}