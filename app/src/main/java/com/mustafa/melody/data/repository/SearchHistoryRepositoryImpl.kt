package com.mustafa.melody.data.repository

import com.mustafa.melody.data.local.dao.SearchHistoryDao
import com.mustafa.melody.data.mapper.toDomain
import com.mustafa.melody.data.mapper.toEntity
import com.mustafa.melody.domain.model.SearchHistoryItem
import com.mustafa.melody.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun observeRecentSearches(limit: Int): Flow<List<SearchHistoryItem>> {
        return searchHistoryDao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveSearch(item: SearchHistoryItem) {
        if (item.query.isBlank()) return
        searchHistoryDao.upsert(item.copy(query = item.query.trim()).toEntity())
    }

    override suspend fun deleteSearch(query: String) {
        searchHistoryDao.deleteByQuery(query)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
    }
}
