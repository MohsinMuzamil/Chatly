package com.mohsin.chatly.data.local

import androidx.room.*
import com.mohsin.chatly.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Query("DELETE FROM messages WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("""
    SELECT * FROM messages 
    WHERE (senderId = :myUserId AND receiverId = :chatUserId) 
       OR (senderId = :chatUserId AND receiverId = :myUserId)
    ORDER BY timestamp ASC
""")
    fun getMessagesWithUser(chatUserId: String, myUserId: String): Flow<List<Message>>
}

