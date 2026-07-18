package com.mustafa.melody.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppDimens.cornerExtraSmall),
    small = RoundedCornerShape(AppDimens.cornerSmall),
    medium = RoundedCornerShape(AppDimens.cornerMedium),
    large = RoundedCornerShape(AppDimens.cornerLarge),
    extraLarge = RoundedCornerShape(AppDimens.cornerExtraLarge)
)
