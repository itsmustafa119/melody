package com.mustafa.melody.presentation.playlists

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.EmptyState
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.SongCard
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.domain.model.Song
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    state: PlaylistDetailsUiState,
    onBack: () -> Unit,
    onPlayQueue: (List<Song>, Int) -> Unit,
    likedSongIds: Set<String>,
    onToggleLike: (String) -> Unit,
    onRetry: () -> Unit,
    pagedSongs: LazyPagingItems<Song>? = null,
) {
    val visibleSongs = pagedSongs?.itemSnapshotList?.items ?: state.songs
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(state.playlist?.title ?: stringResource(R.string.playlist)) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = AppDimens.spacingMedium)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                Button(enabled = visibleSongs.isNotEmpty(), onClick = { onPlayQueue(visibleSongs, 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PlayArrow, null); Text(stringResource(R.string.play_all))
                }
                OutlinedButton(enabled = visibleSongs.isNotEmpty(), onClick = { onPlayQueue(visibleSongs.shuffled(), 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Shuffle, null); Text(stringResource(R.string.shuffle))
                }
            }
            when {
                state.isLoading || pagedSongs?.loadState?.refresh is LoadState.Loading -> repeat(6) { SongCardShimmer() }
                state.errorMessageResId != null -> ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(state.errorMessageResId),
                    onRetry = onRetry,
                )
                (pagedSongs?.itemCount ?: state.songs.size) == 0 -> EmptyState(title = stringResource(R.string.no_songs_in_playlist))
                else -> LazyColumn {
                    if (pagedSongs != null) items(
                        count = pagedSongs.itemCount,
                        key = pagedSongs.itemKey { it.id },
                    ) { index ->
                        val song = pagedSongs[index] ?: return@items
                        SongCard(
                            title = song.title,
                            artistName = song.artistName,
                            coverImageUrl = song.coverImageUrl,
                            isLiked = song.id in likedSongIds,
                            onClick = { onPlayQueue(visibleSongs, visibleSongs.indexOf(song).coerceAtLeast(0)) },
                            onLikeClick = { onToggleLike(song.id) },
                        )
                    } else items(state.songs, key = { it.id }) { song ->
                        SongCard(
                            title = song.title,
                            artistName = song.artistName,
                            coverImageUrl = song.coverImageUrl,
                            isLiked = song.id in likedSongIds,
                            onClick = { onPlayQueue(state.songs, state.songs.indexOf(song)) },
                            onLikeClick = { onToggleLike(song.id) },
                        )
                    }
                }
            }
        }
    }
}
