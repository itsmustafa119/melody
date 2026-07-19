package com.mustafa.melody.presentation.app

import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.ThemeMode

data class AppUiState(
    val isLoading: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val fontScale: AppFontScale = AppFontScale.DEFAULT,
    val isPremium: Boolean = false
)
