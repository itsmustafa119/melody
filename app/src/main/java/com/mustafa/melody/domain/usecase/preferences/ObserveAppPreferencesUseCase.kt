package com.mustafa.melody.domain.usecase.preferences

import com.mustafa.melody.domain.model.AppPreferences
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAppPreferencesUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    operator fun invoke(): Flow<AppPreferences> = repository.preferences
}
