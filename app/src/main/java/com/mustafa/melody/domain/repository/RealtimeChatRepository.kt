package com.mustafa.melody.domain.repository

import com.mustafa.melody.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface RealtimeChatRepository {
    fun observeConversation(otherUserId: String): Flow<List<ChatMessage>>
    fun observeTyping(otherUserId: String): Flow<Boolean>
    suspend fun send(otherUserId: String, text: String?, sharedSongId: String?)
    suspend fun sendTyping(otherUserId: String, isTyping: Boolean)
}
