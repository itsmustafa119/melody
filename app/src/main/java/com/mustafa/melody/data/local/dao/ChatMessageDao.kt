package com.mustafa.melody.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.melody.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Upsert
    suspend fun upsert(message: ChatMessageEntity)

    @Upsert
    suspend fun upsertAll(messages: List<ChatMessageEntity>)

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE conversation_id = :conversationId
        ORDER BY sent_at ASC
        """
    )
    fun observeConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE conversation_id = :conversationId
        ORDER BY sent_at DESC
        """
    )
    fun conversationPagingSource(conversationId: String): PagingSource<Int, ChatMessageEntity>

    @Query(
        """
        UPDATE chat_messages
        SET status = :status
        WHERE message_id = :messageId
        """
    )
    suspend fun updateStatus(messageId: String, status: String)

    @Query("DELETE FROM chat_messages WHERE conversation_id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("DELETE FROM chat_messages WHERE message_id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE conversation_id = :conversationId")
    suspend fun countConversationMessages(conversationId: String): Int
}
