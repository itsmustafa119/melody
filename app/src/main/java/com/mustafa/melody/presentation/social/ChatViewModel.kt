package com.mustafa.melody.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.domain.repository.RealtimeChatRepository
import com.mustafa.melody.domain.usecase.chat.ObserveConversationMessagesUseCase
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mustafa.melody.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: RealtimeChatRepository,
    observeConversationMessages: ObserveConversationMessagesUseCase,
) : ViewModel() {
    private val conversationId = MutableStateFlow("")
    private var typingStopJob: Job? = null

    val messages = conversationId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(PagingData.empty<ChatMessage>()) else observeConversationMessages(id)
    }.cachedIn(viewModelScope)

    val isOtherUserTyping = conversationId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(false) else repository.observeTyping(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private var syncJob: Job? = null

    fun open(userId: String) {
        conversationId.value = userId
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            repository.observeConversation(userId).collectLatest { /* Realtime rows are persisted to Room. */ }
        }
    }

    fun send(text: String?, sharedSongId: String? = null) {
        if (text.isNullOrBlank() && sharedSongId == null) return
        viewModelScope.launch {
            repository.send(conversationId.value, text, sharedSongId)
            repository.sendTyping(conversationId.value, false)
        }
    }

    fun onTypingChanged(hasText: Boolean) {
        val otherId = conversationId.value
        if (otherId.isBlank()) return
        typingStopJob?.cancel()
        viewModelScope.launch { repository.sendTyping(otherId, hasText) }
        if (hasText) typingStopJob = viewModelScope.launch {
            delay(1_200)
            repository.sendTyping(otherId, false)
        }
    }
}
