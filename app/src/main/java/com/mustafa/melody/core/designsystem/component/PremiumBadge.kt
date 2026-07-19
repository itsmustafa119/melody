package com.mustafa.melody.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.PremiumGoldDark
import com.mustafa.melody.core.designsystem.theme.PremiumGoldLight

@Composable
fun PremiumBadge(
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSystemInDarkTheme()) PremiumGoldDark else PremiumGoldLight
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimens.cornerExtraSmall))
            .background(backgroundColor)
            .padding(
                horizontal = AppDimens.badgeHorizontalPadding,
                vertical = AppDimens.badgeVerticalPadding
            )
    ) {
        Text(
            text = stringResource(R.string.premium).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black // Gold badge usually looks best with black text
        )
    }
}
