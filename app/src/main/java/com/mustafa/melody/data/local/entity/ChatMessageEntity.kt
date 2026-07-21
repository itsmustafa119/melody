package com.mustafa.melody.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["conversation_id"]),
        Index(value = ["sender_id"]),
        Index(value = ["receiver_id"]),
        Index(value = ["sent_at"]),
        Index(value = ["status"]),
        Index(value = ["conversation_id", "sent_at"])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "sender_id")
    val senderId: String,

    @ColumnInfo(name = "receiver_id")
    val receiverId: String,

    @ColumnInfo(name = "text")
    val text: String?,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "shared_song_id")
    val sharedSongId: String?
)
