package com.mustafa.melody.domain.usecase.preferences

import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}
