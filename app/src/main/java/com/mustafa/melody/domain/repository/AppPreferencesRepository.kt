package com.mustafa.melody.domain.repository

import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.AppPreferences
import com.mustafa.melody.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {

    val preferences: Flow<AppPreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setLanguage(language: AppLanguage)

    suspend fun setFontScale(fontScale: AppFontScale)

    suspend fun setPremiumStatus(isPremium: Boolean)
}
