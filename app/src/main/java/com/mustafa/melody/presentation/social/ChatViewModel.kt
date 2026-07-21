package com.mustafa.melody.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.domain.repository.RealtimeChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: RealtimeChatRepository,
) : ViewModel() {
    private val conversationId = MutableStateFlow("")
    private var typingStopJob: Job? = null

    val messages = conversationId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(emptyList()) else repository.observeConversation(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isOtherUserTyping = conversationId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(false) else repository.observeTyping(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun open(userId: String) { conversationId.value = userId }

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
