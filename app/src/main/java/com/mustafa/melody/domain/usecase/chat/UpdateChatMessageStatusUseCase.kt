package com.mustafa.melody.domain.usecase.chat

import com.mustafa.melody.domain.model.MessageStatus
import com.mustafa.melody.domain.repository.ChatRepository
import javax.inject.Inject

class UpdateChatMessageStatusUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messageId: String, status: MessageStatus) =
        repository.updateMessageStatus(messageId, status)
}
