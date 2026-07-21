package com.mustafa.melody.domain.usecase.searchhistory

import com.mustafa.melody.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchQueryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) = repository.deleteSearch(query)
}
