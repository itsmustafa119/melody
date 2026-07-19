package com.mustafa.melody.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.mustafa.melody.core.designsystem.theme.AppDimens

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AppDimens.shimmerCornerRadius)
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun SongCardShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimens.spacingSmall)
    ) {
        ShimmerBox(modifier = Modifier.size(AppDimens.albumCoverSmall))
        Spacer(modifier = Modifier.width(AppDimens.spacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(modifier = Modifier.height(20.dp).fillMaxWidth(0.7f))
            Spacer(modifier = Modifier.height(AppDimens.spacingExtraSmall))
            ShimmerBox(modifier = Modifier.height(16.dp).fillMaxWidth(0.4f))
        }
    }
}

@Composable
fun PlaylistCardShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(AppDimens.cardWidthMedium)) {
        ShimmerBox(modifier = Modifier.size(AppDimens.cardWidthMedium))
        Spacer(modifier = Modifier.height(AppDimens.spacingSmall))
        ShimmerBox(modifier = Modifier.height(20.dp).fillMaxWidth(0.8f))
    }
}
