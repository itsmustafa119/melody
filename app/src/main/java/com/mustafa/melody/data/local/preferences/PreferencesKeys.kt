package com.mustafa.melody.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferencesKeys {
    val themeMode = stringPreferencesKey("theme_mode")
    val language = stringPreferencesKey("app_language")
    val fontScale = stringPreferencesKey("font_scale")
    val isPremium = booleanPreferencesKey("is_premium")
}
