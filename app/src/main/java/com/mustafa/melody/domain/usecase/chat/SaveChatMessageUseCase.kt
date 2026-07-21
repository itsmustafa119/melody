package com.mustafa.melody.domain.usecase.chat

import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.repository.ChatRepository
import javax.inject.Inject

class SaveChatMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: ChatMessage) = repository.saveMessage(message)
}
