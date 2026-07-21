package com.mustafa.melody.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.R
import com.mustafa.melody.domain.model.SocialProfile
import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.repository.SocialRepository
import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SocialUiState(
    val query: String = "",
    val users: List<SocialProfile> = emptyList(),
    val followingIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val errorMessageResId: Int? = null,
    val conversations: List<ConversationPreview> = emptyList(),
    val publicPlaylistUserId: String? = null,
    val publicPlaylists: List<MusicPlaylist> = emptyList(),
)

data class ConversationPreview(
    val profile: SocialProfile,
    val lastMessage: String?,
    val sentAt: Long,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: SocialRepository,
    chatMessageDao: ChatMessageDao,
    provider: SupabaseProvider,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val users = MutableStateFlow<List<SocialProfile>>(emptyList())
    private val following = MutableStateFlow<Set<String>>(emptySet())
    private val loading = MutableStateFlow(true)
    private val error = MutableStateFlow<Int?>(null)
    private val publicPlaylistUserId = MutableStateFlow<String?>(null)
    private val publicPlaylists = MutableStateFlow<List<MusicPlaylist>>(emptyList())

    private val baseState = combine(query, users, following, loading, error) { q, people, followed, busy, failure ->
        SocialUiState(q, people, followed, busy, failure)
    }

    private val currentUserId = provider.client?.auth?.currentUserOrNull()?.id ?: "me"
    private val cachedMessages = chatMessageDao.observeAllMessages()

    private val stateWithConversations = combine(baseState, cachedMessages) { base, messages ->
        val previews = messages
            .distinctBy { it.conversationId }
            .map { message ->
                val otherId = if (message.senderId == currentUserId) message.receiverId else message.senderId
                val profile = base.users.firstOrNull { it.id == otherId }
                    ?: SocialProfile(
                        id = otherId,
                        username = otherId.take(12),
                        displayName = otherId.take(12),
                        avatarUrl = null,
                        bio = "",
                        isPremium = false,
                    )
                ConversationPreview(profile, message.text, message.sentAt)
            }
        base.copy(conversations = previews)
    }

    val state = combine(stateWithConversations, publicPlaylistUserId, publicPlaylists) { base, userId, playlists ->
        base.copy(publicPlaylistUserId = userId, publicPlaylists = playlists)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SocialUiState())

    init {
        viewModelScope.launch { query.debounce(350).collect(::load) }
    }

    fun query(value: String) { query.value = value }
    fun retry() { viewModelScope.launch { load(query.value) } }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            val isFollowing = userId in following.value
            runCatching {
                if (isFollowing) repository.unfollow(userId) else repository.follow(userId)
            }.onSuccess {
                following.value = if (isFollowing) following.value - userId else following.value + userId
            }.onFailure { error.value = R.string.social_action_failed }
        }
    }

    fun viewPublicPlaylists(userId: String) {
        if (publicPlaylistUserId.value == userId) {
            publicPlaylistUserId.value = null
            publicPlaylists.value = emptyList()
            return
        }
        publicPlaylistUserId.value = userId
        publicPlaylists.value = emptyList()
        viewModelScope.launch {
            publicPlaylists.value = repository.publicPlaylists(userId)
        }
    }

    private suspend fun load(value: String) {
        loading.value = true
        error.value = null
        runCatching { repository.searchUsers(value) to repository.followingIds() }
            .onSuccess { (people, followed) -> users.value = people; following.value = followed }
            .onFailure { error.value = R.string.social_load_failed }
        loading.value = false
    }
}
