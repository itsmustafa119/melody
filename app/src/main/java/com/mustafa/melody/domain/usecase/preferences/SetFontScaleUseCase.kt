package com.mustafa.melody.domain.usecase.preferences

import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import javax.inject.Inject

class SetFontScaleUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(fontScale: AppFontScale) = repository.setFontScale(fontScale)
}
