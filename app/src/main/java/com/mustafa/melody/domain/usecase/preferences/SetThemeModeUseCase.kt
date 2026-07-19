package com.mustafa.melody.domain.usecase.preferences

import com.mustafa.melody.domain.model.ThemeMode
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) = repository.setThemeMode(themeMode)
}
