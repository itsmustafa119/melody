package com.mustafa.melody.data.repository

import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.mapper.toDomain
import com.mustafa.melody.data.mapper.toEntity
import com.mustafa.melody.data.remote.SupabaseProvider
import com.mustafa.melody.data.remote.dto.MessageDto
import com.mustafa.melody.data.remote.dto.MessageInsertDto
import com.mustafa.melody.data.remote.dto.MessageReceiptDto
import com.mustafa.melody.data.remote.dto.TypingEvent
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus
import com.mustafa.melody.domain.repository.RealtimeChatRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import io.github.jan.supabase.annotations.SupabaseExperimental

@OptIn(SupabaseExperimental::class)
@Singleton
class RealtimeChatRepositoryImpl @Inject constructor(
    private val provider: SupabaseProvider,
    private val dao: ChatMessageDao,
) : RealtimeChatRepository {
    private val typingChannels = mutableMapOf<String, RealtimeChannel>()

    override fun observeConversation(otherUserId: String): Flow<List<ChatMessage>> = channelFlow {
        launch {
            dao.observeConversation(otherUserId).collectLatest { rows -> send(rows.map { it.toDomain() }) }
        }
        val client = provider.client
        val currentId = client?.auth?.currentUserOrNull()?.id
        if (client != null && currentId != null) launch {
            client.from("messages").selectAsFlow(MessageDto::id).collectLatest { rows ->
                val conversation = rows.filter { row ->
                    (row.senderId == currentId && row.recipientId == otherUserId) ||
                        (row.senderId == otherUserId && row.recipientId == currentId)
                }
                dao.upsertAll(conversation.map { it.toDomain(currentId, otherUserId).toEntity() })
                conversation.filter { it.recipientId == currentId && it.status != MessageStatus.READ.name }.forEach { incoming ->
                    runCatching {
                        client.from("messages").update(MessageReceiptDto(MessageStatus.READ.name, Instant.now().toString())) {
                            filter { eq("id", incoming.id) }
                        }
                    }
                }
            }
        }
        awaitClose()
    }.distinctUntilChanged()

    override fun observeTyping(otherUserId: String): Flow<Boolean> = channelFlow {
        val client = provider.client
        val currentId = client?.auth?.currentUserOrNull()?.id
        if (client == null || currentId == null) {
            send(false)
            close()
            return@channelFlow
        }
        val channel = client.channel(topic(currentId, otherUserId))
        typingChannels[otherUserId] = channel
        val events = channel.broadcastFlow<TypingEvent>(event = TYPING_EVENT)
        channel.subscribe()
        launch {
            events.collectLatest { event -> if (event.senderId == otherUserId) send(event.isTyping) }
        }
        awaitClose {
            typingChannels.remove(otherUserId)
            launch { channel.unsubscribe() }
        }
    }.distinctUntilChanged()

    override suspend fun send(otherUserId: String, text: String?, sharedSongId: String?) {
        val id = UUID.randomUUID().toString()
        val client = provider.client
        val currentId = client?.auth?.currentUserOrNull()?.id
        val local = ChatMessage(
            messageId = id,
            conversationId = otherUserId,
            senderId = "me",
            receiverId = otherUserId,
            text = text?.trim(),
            sentAt = System.currentTimeMillis(),
            status = MessageStatus.SENDING,
            sharedSongId = sharedSongId,
        )
        dao.upsert(local.toEntity())
        if (client == null || currentId == null) {
            delay(300)
            dao.updateStatus(id, MessageStatus.SENT.name)
            return
        }
        runCatching {
            client.from("messages").insert(
                MessageInsertDto(id, currentId, otherUserId, text?.trim().orEmpty(), sharedSongId),
            )
        }.onSuccess { dao.updateStatus(id, MessageStatus.SENT.name) }
            .onFailure { dao.updateStatus(id, MessageStatus.FAILED.name) }
    }

    override suspend fun sendTyping(otherUserId: String, isTyping: Boolean) {
        val client = provider.client ?: return
        val currentId = client.auth.currentUserOrNull()?.id ?: return
        val channel = typingChannels[otherUserId] ?: client.channel(topic(currentId, otherUserId)).also {
            typingChannels[otherUserId] = it
            it.subscribe(blockUntilSubscribed = true)
        }
        channel.broadcast(
            event = TYPING_EVENT,
            message = buildJsonObject {
                put("sender_id", currentId)
                put("is_typing", isTyping)
            },
        )
    }

    private fun topic(first: String, second: String) = "typing:${listOf(first, second).sorted().joinToString(":")}" 

    private companion object { const val TYPING_EVENT = "typing" }
}
