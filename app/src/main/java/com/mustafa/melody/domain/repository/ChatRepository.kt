package com.mustafa.melody.domain.repository

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeConversationMessages(conversationId: String): Flow<PagingData<ChatMessage>>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun saveMessages(messages: List<ChatMessage>)
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun deleteMessage(messageId: String)
    suspend fun clearConversation(conversationId: String)
}
