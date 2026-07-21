package com.mustafa.melody.domain.usecase.searchhistory

import com.mustafa.melody.domain.model.SearchHistoryItem
import com.mustafa.melody.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SaveSearchQueryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String, searchedAt: Long) {
        repository.saveSearch(SearchHistoryItem(query, searchedAt))
    }
}
