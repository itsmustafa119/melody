package com.mustafa.melody.domain.usecase.searchhistory

import com.mustafa.melody.domain.model.SearchHistoryItem
import com.mustafa.melody.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecentSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(limit: Int): Flow<List<SearchHistoryItem>> = repository.observeRecentSearches(limit)
}
