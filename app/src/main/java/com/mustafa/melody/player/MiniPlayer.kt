package com.mustafa.melody.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mustafa.melody.core.designsystem.theme.AppDimens

@Composable
fun MiniPlayer(
    state: PlaybackUiState,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    artworkModifier: Modifier = Modifier,
) {
    val song = state.song ?: return
    Surface(tonalElevation = AppDimens.spacingSmall, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.clickable(onClick = onClick)) {
            if (state.durationMs > 0) {
                LinearProgressIndicator(
                    progress = { (state.positionMs.toFloat() / state.durationMs).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.spacingMedium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = song.coverImageUrl,
                    contentDescription = song.title,
                    modifier = artworkModifier
                        .size(AppDimens.albumCoverSmall)
                        .clip(RoundedCornerShape(AppDimens.cornerSmall)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(AppDimens.spacingSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(song.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artistName, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onToggle) {
                    Icon(if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}
