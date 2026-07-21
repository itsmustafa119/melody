package com.mustafa.melody.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.local.database.DatabaseConstants
import com.mustafa.melody.data.mapper.toDomain
import com.mustafa.melody.data.mapper.toEntity
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus
import com.mustafa.melody.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ChatRepository {

    override fun observeConversationMessages(conversationId: String): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(
                pageSize = DatabaseConstants.DEFAULT_PAGE_SIZE,
                prefetchDistance = DatabaseConstants.DEFAULT_PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { chatMessageDao.conversationPagingSource(conversationId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        chatMessageDao.upsert(message.toEntity())
    }

    override suspend fun saveMessages(messages: List<ChatMessage>) {
        chatMessageDao.upsertAll(messages.map { it.toEntity() })
    }

    override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) {
        chatMessageDao.updateStatus(messageId, status.name)
    }

    override suspend fun deleteMessage(messageId: String) {
        chatMessageDao.deleteMessage(messageId)
    }

    override suspend fun clearConversation(conversationId: String) {
        chatMessageDao.deleteConversation(conversationId)
    }
}
