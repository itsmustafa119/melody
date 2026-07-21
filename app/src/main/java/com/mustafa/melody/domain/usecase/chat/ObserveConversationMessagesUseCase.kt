package com.mustafa.melody.domain.usecase.chat

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConversationMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(conversationId: String): Flow<PagingData<ChatMessage>> =
        repository.observeConversationMessages(conversationId)
}
