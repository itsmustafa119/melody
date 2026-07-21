package com.mustafa.melody.presentation.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.toBitmap
import androidx.palette.graphics.Palette
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.player.PlaybackUiState

@Composable
fun NowPlayingScreen(
    state: PlaybackUiState,
    speed: Float,
    onBack: () -> Unit,
    onToggle: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onSpeed: (Float) -> Unit,
    onSleepTimer: () -> Unit,
    onDownload: () -> Unit,
) {
    val song = state.song ?: return
    val transition = rememberInfiniteTransition(label = "cover")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (state.isPlaying) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(18_000, easing = LinearEasing)),
        label = "rotation",
    )
    val visualizerPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(tween(1_400, easing = LinearEasing)),
        label = "visualizer",
    )
    var dominantColor by remember(song.id) { mutableStateOf(Color(0xFF24143F)) }
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(dominantColor.copy(alpha = 0.82f), MaterialTheme.colorScheme.background),
                    ),
                )
                .padding(padding)
                .padding(AppDimens.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.KeyboardArrowDown, stringResource(R.string.back)) }
                Text(stringResource(R.string.now_playing), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Row {
                    IconButton(onClick = onDownload) { Icon(Icons.Default.Download, stringResource(R.string.download_song)) }
                    IconButton(onClick = onSleepTimer) { Icon(Icons.Default.Timer, stringResource(R.string.sleep_timer)) }
                }
            }
            Spacer(Modifier.height(32.dp))
            AsyncImage(
                model = song.coverImageUrl,
                contentDescription = song.title,
                modifier = Modifier.size(280.dp).rotate(rotation).clip(CircleShape),
                contentScale = ContentScale.Crop,
                onSuccess = { result ->
                    Palette.from(result.result.image.toBitmap()).generate { palette ->
                        palette?.getDominantColor(android.graphics.Color.rgb(36, 20, 63))?.let {
                            dominantColor = Color(it)
                        }
                    }
                },
            )
            Spacer(Modifier.height(AppDimens.spacingLarge))
            Text(song.title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(song.artistName, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(AppDimens.spacingMedium))
            Canvas(Modifier.fillMaxWidth().height(50.dp)) {
                val bars = 32
                val barWidth = size.width / (bars * 2)
                repeat(bars) { index ->
                    val wave = kotlin.math.sin(visualizerPhase + index * 0.62f)
                    val factor = if (state.isPlaying) (0.28f + kotlin.math.abs(wave) * 0.72f) else 0.22f
                    drawLine(
                        color = Color(0xFF7C4DFF),
                        start = androidx.compose.ui.geometry.Offset(index * barWidth * 2, size.height / 2 - size.height * factor / 2),
                        end = androidx.compose.ui.geometry.Offset(index * barWidth * 2, size.height / 2 + size.height * factor / 2),
                        strokeWidth = barWidth,
                    )
                }
            }
            Slider(
                value = state.positionMs.toFloat().coerceAtMost(state.durationMs.toFloat().coerceAtLeast(1f)),
                onValueChangeFinished = {},
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..state.durationMs.toFloat().coerceAtLeast(1f),
            )
            if (state.queueSize > 0) {
                Text(
                    stringResource(R.string.queue_position, state.currentIndex + 1, state.queueSize),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrevious, enabled = state.hasPrevious || state.positionMs > 0) {
                    Icon(Icons.Default.SkipPrevious, stringResource(R.string.previous_song), Modifier.size(36.dp))
                }
                IconButton(onClick = onToggle, modifier = Modifier.size(72.dp)) {
                    Icon(
                        if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        stringResource(if (state.isPlaying) R.string.pause else R.string.play_song),
                        modifier = Modifier.size(48.dp),
                    )
                }
                IconButton(onClick = onNext, enabled = state.hasNext) {
                    Icon(Icons.Default.SkipNext, stringResource(R.string.next_song), Modifier.size(36.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                listOf(0.75f, 1f, 1.25f, 1.5f, 2f).forEach { value ->
                    FilterChip(
                        selected = speed == value,
                        onClick = { onSpeed(value) },
                        label = { Text("${value}x") },
                    )
                }
            }
        }
    }
}
