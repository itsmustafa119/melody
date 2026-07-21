package com.mustafa.melody.domain.repository

import com.mustafa.melody.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeRecentSearches(limit: Int): Flow<List<SearchHistoryItem>>
    suspend fun saveSearch(item: SearchHistoryItem)
    suspend fun deleteSearch(query: String)
    suspend fun clearSearchHistory()
}
