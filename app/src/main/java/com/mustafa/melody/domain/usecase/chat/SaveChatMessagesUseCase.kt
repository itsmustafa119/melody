package com.mustafa.melody.domain.usecase.chat

import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.repository.ChatRepository
import javax.inject.Inject

class SaveChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messages: List<ChatMessage>) = repository.saveMessages(messages)
}
