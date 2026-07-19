package com.mustafa.melody.domain.usecase.preferences

import com.mustafa.melody.domain.repository.AppPreferencesRepository
import javax.inject.Inject

class SetPremiumStatusUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(isPremium: Boolean) = repository.setPremiumStatus(isPremium)
}
