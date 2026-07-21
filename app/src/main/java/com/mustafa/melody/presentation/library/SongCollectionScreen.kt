package com.mustafa.melody.presentation.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.EmptyState
import com.mustafa.melody.core.designsystem.component.SongCard
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.domain.model.Song
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCollectionScreen(
    title: String,
    songs: List<Song>,
    onBack: () -> Unit,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onLikeToggle: ((Song) -> Unit)? = null,
    onSwipeRemove: ((Song) -> Unit)? = null,
    pagedSongs: LazyPagingItems<Song>? = null,
) {
    val visibleSongs = pagedSongs?.itemSnapshotList?.items ?: songs
    Scaffold(topBar = { TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
    ) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = AppDimens.spacingMedium)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                Button(enabled = visibleSongs.isNotEmpty(), onClick = { onPlayQueue(visibleSongs, 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PlayArrow, null); Text(stringResource(R.string.play_all))
                }
                OutlinedButton(enabled = visibleSongs.isNotEmpty(), onClick = { onPlayQueue(visibleSongs.shuffled(), 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Shuffle, null); Text(stringResource(R.string.shuffle))
                }
            }
            if (pagedSongs?.loadState?.refresh is LoadState.Loading) repeat(5) { com.mustafa.melody.core.designsystem.component.SongCardShimmer() }
            else if ((pagedSongs?.itemCount ?: songs.size) == 0) EmptyState(title = stringResource(R.string.nothing_here_yet))
            else LazyColumn {
                if (pagedSongs != null) items(
                    count = pagedSongs.itemCount,
                    key = pagedSongs.itemKey { it.id },
                ) { index ->
                    val song = pagedSongs[index] ?: return@items
                    CollectionSongRow(song, visibleSongs, onPlayQueue, onLikeToggle, onSwipeRemove)
                } else items(songs, key = { it.id }) { song ->
                    CollectionSongRow(song, songs, onPlayQueue, onLikeToggle, onSwipeRemove)
                }
            }
        }
    }
}

@Composable
private fun CollectionSongRow(
    song: Song,
    queue: List<Song>,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onLikeToggle: ((Song) -> Unit)?,
    onSwipeRemove: ((Song) -> Unit)?,
) {
                    val dismissState = rememberSwipeToDismissBoxState()
                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) onSwipeRemove?.invoke(song)
                    }
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = onSwipeRemove != null,
                        enableDismissFromEndToStart = onSwipeRemove != null,
                        backgroundContent = {
                            Box(
                                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    stringResource(R.string.remove_song),
                                    Modifier.padding(AppDimens.spacingLarge),
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        },
                    ) {
                        SongCard(
                            title = song.title,
                            artistName = song.artistName,
                            coverImageUrl = song.coverImageUrl,
                            isLiked = song.isLiked || onLikeToggle != null,
                            showLikeAction = onLikeToggle != null,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            onClick = { onPlayQueue(queue, queue.indexOf(song).coerceAtLeast(0)) },
                            onLikeClick = { onLikeToggle?.invoke(song) },
                        )
                    }
}
