package com.mustafa.melody.domain.usecase.chat

import com.mustafa.melody.domain.repository.ChatRepository
import javax.inject.Inject

class ClearConversationUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String) = repository.clearConversation(conversationId)
}
