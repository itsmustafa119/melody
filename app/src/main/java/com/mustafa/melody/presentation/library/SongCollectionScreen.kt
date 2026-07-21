package com.mustafa.melody.presentation.library

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
import com.mustafa.melody.core.designsystem.component.SongCard
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCollectionScreen(
    title: String,
    songs: List<Song>,
    onBack: () -> Unit,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onRemove: ((Song) -> Unit)? = null,
) {
    Scaffold(topBar = { TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
    ) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = AppDimens.spacingMedium)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                Button(enabled = songs.isNotEmpty(), onClick = { onPlayQueue(songs, 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PlayArrow, null); Text(stringResource(R.string.play_all))
                }
                OutlinedButton(enabled = songs.isNotEmpty(), onClick = { onPlayQueue(songs.shuffled(), 0) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Shuffle, null); Text(stringResource(R.string.shuffle))
                }
            }
            if (songs.isEmpty()) EmptyState(title = stringResource(R.string.nothing_here_yet))
            else LazyColumn {
                items(songs, key = { it.id }) { song ->
                    SongCard(
                        title = song.title, artistName = song.artistName, coverImageUrl = song.coverImageUrl,
                        isLiked = song.isLiked || onRemove != null,
                        showLikeAction = onRemove != null,
                        onClick = { onPlayQueue(songs, songs.indexOf(song)) },
                        onLikeClick = { onRemove?.invoke(song) },
                    )
                }
            }
        }
    }
}
