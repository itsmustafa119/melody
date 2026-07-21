package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.ChatMessageEntity
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus

fun ChatMessageEntity.toDomain() = ChatMessage(
    messageId = messageId,
    conversationId = conversationId,
    senderId = senderId,
    receiverId = receiverId,
    text = text,
    sentAt = sentAt,
    status = MessageStatus.entries.firstOrNull { it.name == status } ?: MessageStatus.FAILED,
    sharedSongId = sharedSongId
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    messageId = messageId,
    conversationId = conversationId,
    senderId = senderId,
    receiverId = receiverId,
    text = text,
    sentAt = sentAt,
    status = status.name,
    sharedSongId = sharedSongId
)
