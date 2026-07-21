package com.mustafa.melody.presentation.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.domain.model.ChatMessage
import com.mustafa.melody.domain.model.MessageStatus
import com.mustafa.melody.domain.model.SocialProfile
import com.mustafa.melody.domain.model.Song
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    state: SocialUiState,
    onBack: () -> Unit,
    onQuery: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onViewPlaylists: (String) -> Unit,
    onPlaylist: (String) -> Unit,
    onChat: (SocialProfile) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.people)) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = AppDimens.spacingMedium)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQuery,
                label = { Text(stringResource(R.string.search_users)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            when {
                state.isLoading -> repeat(6) { SongCardShimmer() }
                state.errorMessageResId != null -> ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(state.errorMessageResId),
                    onRetry = onRetry,
                )
                else -> LazyColumn {
                    item {
                        Text(
                            stringResource(R.string.recent_conversations),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = AppDimens.spacingSmall),
                        )
                    }
                    if (state.conversations.isEmpty()) {
                        item { Text(stringResource(R.string.no_conversations), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        items(state.conversations, key = { "conversation-${it.profile.id}" }) { conversation ->
                            ListItem(
                                headlineContent = { Text(conversation.profile.displayName) },
                                supportingContent = { Text(conversation.lastMessage ?: stringResource(R.string.shared_song)) },
                                modifier = Modifier.clickable { onChat(conversation.profile) },
                            )
                        }
                    }
                    item {
                        Text(
                            stringResource(R.string.people),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = AppDimens.spacingSmall),
                        )
                    }
                    items(state.users, key = { it.id }) { user ->
                        Column {
                            ListItem(
                                headlineContent = { Text(user.displayName) },
                                supportingContent = { Text("@${user.username}") },
                                modifier = Modifier.clickable { onChat(user) },
                                trailingContent = {
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (user.id in state.followingIds) {
                                            OutlinedButton(onClick = { onToggleFollow(user.id) }) { Text(stringResource(R.string.unfollow)) }
                                        } else {
                                            Button(onClick = { onToggleFollow(user.id) }) { Text(stringResource(R.string.follow)) }
                                        }
                                        OutlinedButton(onClick = { onViewPlaylists(user.id) }) {
                                            Text(stringResource(R.string.public_playlists))
                                        }
                                    }
                                },
                            )
                            if (state.publicPlaylistUserId == user.id) {
                                if (state.publicPlaylists.isEmpty()) {
                                    Text(
                                        stringResource(R.string.no_public_playlists),
                                        modifier = Modifier.padding(horizontal = AppDimens.spacingMedium),
                                    )
                                } else state.publicPlaylists.forEach { playlist ->
                                    ListItem(
                                        headlineContent = { Text(playlist.title) },
                                        supportingContent = { Text(playlist.subtitle) },
                                        modifier = Modifier.clickable { onPlaylist(playlist.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    userName: String,
    messages: LazyPagingItems<ChatMessage>,
    isOtherUserTyping: Boolean,
    currentSong: Song?,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onShareSong: (String) -> Unit,
    onTypingChanged: (Boolean) -> Unit,
    onPlaySharedSong: (String) -> Unit,
) {
    var text by remember(userId) { mutableStateOf("") }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text(userName)
                    Text(stringResource(R.string.online), style = MaterialTheme.typography.labelSmall)
                }
            },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(AppDimens.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall),
                reverseLayout = true,
            ) {
                if (isOtherUserTyping) item { Text(stringResource(R.string.user_is_typing, userName)) }
                items(
                    count = messages.itemCount,
                    key = messages.itemKey { it.messageId },
                ) { index ->
                    val message = messages[index] ?: return@items
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = if (message.senderId == "me") Alignment.End else Alignment.Start,
                    ) {
                        Surface(
                            color = if (message.senderId == "me") MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Column(Modifier.padding(AppDimens.spacingSmall)) {
                                message.text?.let { Text(it) }
                                message.sharedSongId?.let { songId ->
                                    Row(
                                        Modifier.clickable { onPlaySharedSong(songId) }.padding(vertical = AppDimens.spacingSmall),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Default.MusicNote, null)
                                        Text(stringResource(R.string.shared_song_card))
                                    }
                                }
                            }
                        }
                        Text(
                            stringResource(
                                when (message.status) {
                                    MessageStatus.SENDING -> R.string.sending
                                    MessageStatus.SENT -> R.string.sent
                                    MessageStatus.DELIVERED -> R.string.delivered
                                    MessageStatus.READ -> R.string.read
                                    MessageStatus.FAILED -> R.string.failed
                                },
                            ),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(AppDimens.spacingSmall), verticalAlignment = Alignment.CenterVertically) {
                IconButton(enabled = currentSong != null, onClick = { currentSong?.let { onShareSong(it.id) } }) {
                    Icon(Icons.Default.MusicNote, stringResource(R.string.share_current_song))
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; onTypingChanged(it.isNotEmpty()) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.type_message)) },
                    singleLine = true,
                )
                IconButton(onClick = { onSend(text); text = "" }) {
                    Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.send))
                }
            }
        }
    }
}
