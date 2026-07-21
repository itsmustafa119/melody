package com.mustafa.melody.domain.model

data class ChatMessage(
    val messageId: String,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val text: String?,
    val sentAt: Long,
    val status: MessageStatus,
    val sharedSongId: String?
)
