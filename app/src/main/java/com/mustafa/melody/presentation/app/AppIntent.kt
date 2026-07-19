package com.mustafa.melody.presentation.app

import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.ThemeMode

sealed interface AppIntent {
    data class ChangeTheme(val themeMode: ThemeMode) : AppIntent
    data class ChangeLanguage(val language: AppLanguage) : AppIntent
    data class ChangeFontScale(val fontScale: AppFontScale) : AppIntent
    data class ChangePremiumStatus(val isPremium: Boolean) : AppIntent
}
