package com.mustafa.melody.domain.model

data class AppPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val fontScale: AppFontScale = AppFontScale.DEFAULT,
    val isPremium: Boolean = false
)
