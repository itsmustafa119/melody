package com.mustafa.melody.data.remote.dto

import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("recipient_id") val recipientId: String,
    val body: String = "",
    @SerialName("shared_song_id") val sharedSongId: String? = null,
    val status: String = "SENT",
    @SerialName("created_at") val createdAt: String,
    @SerialName("read_at") val readAt: String? = null,
) {
    fun toDomain(currentUserId: String, otherUserId: String) = ChatMessage(
        messageId = id,
        conversationId = otherUserId,
        senderId = if (senderId == currentUserId) "me" else senderId,
        receiverId = if (recipientId == currentUserId) "me" else recipientId,
        text = body.ifBlank { null },
        sentAt = runCatching { Instant.parse(createdAt).toEpochMilli() }.getOrElse { System.currentTimeMillis() },
        status = MessageStatus.entries.firstOrNull { it.name == status } ?: MessageStatus.SENT,
        sharedSongId = sharedSongId,
    )
}

@Serializable
data class MessageInsertDto(
    val id: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("recipient_id") val recipientId: String,
    val body: String,
    @SerialName("shared_song_id") val sharedSongId: String?,
    val status: String = "SENT",
)

@Serializable
data class MessageReceiptDto(
    val status: String,
    @SerialName("read_at") val readAt: String,
)

@Serializable
data class TypingEvent(
    @SerialName("sender_id") val senderId: String,
    @SerialName("is_typing") val isTyping: Boolean,
)
