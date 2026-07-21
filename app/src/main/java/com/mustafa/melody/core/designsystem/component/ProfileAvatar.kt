package com.mustafa.melody.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.theme.AppDimens

@Composable
fun ProfileAvatar(
    displayName: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(AppDimens.profileImageSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = AppDimens.avatarBorderWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.profile_picture),
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            val firstChar = displayName.firstOrNull()?.toString()
            if (firstChar != null && firstChar.isNotBlank()) {
                Text(
                    text = firstChar.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile_picture),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
