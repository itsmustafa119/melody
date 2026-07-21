package com.mustafa.melody.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.AppPreferences
import com.mustafa.melody.domain.model.ThemeMode
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AppPreferencesRepository {

    override val preferences: Flow<AppPreferences> = context.appPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeMode = preferences[PreferencesKeys.themeMode]?.let { storedValue ->
                ThemeMode.entries.firstOrNull { it.name == storedValue }
            } ?: ThemeMode.SYSTEM

            val language = preferences[PreferencesKeys.language]?.let { storedValue ->
                AppLanguage.entries.firstOrNull { it.name == storedValue }
            } ?: AppLanguage.SYSTEM

            val fontScale = preferences[PreferencesKeys.fontScale]?.let { storedValue ->
                AppFontScale.entries.firstOrNull { it.name == storedValue }
            } ?: AppFontScale.DEFAULT

            val isPremium = preferences[PreferencesKeys.isPremium] ?: false

            AppPreferences(
                themeMode = themeMode,
                language = language,
                fontScale = fontScale,
                isPremium = isPremium
            )
        }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.themeMode] = themeMode.name
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.language] = language.name
        }
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.fontScale] = fontScale.name
        }
    }

    override suspend fun setPremiumStatus(isPremium: Boolean) {
        context.appPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.isPremium] = isPremium
        }
    }
}
