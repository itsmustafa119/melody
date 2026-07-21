package com.mustafa.melody.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.data.local.dao.SearchHistoryDao
import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import androidx.paging.cachedIn
import com.mustafa.melody.domain.repository.CatalogFilter
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import com.mustafa.melody.domain.model.Song
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val historyDao: SearchHistoryDao,
    private val catalogRepository: MusicCatalogRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(SearchFilter.ALL)
    private val debouncedQuery = query.debounce(350)

    val state = combine(query, debouncedQuery, filter, historyDao.observeRecent(20)) { live, debounced, selected, history ->
        val normalized = debounced.trim()
        SearchUiState(
            query = live,
            selectedFilter = selected,
            searchHistory = history.map { it.searchQuery },
            isLoading = live.isNotBlank() && live.trim() != normalized,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    val pagedResults = combine(debouncedQuery, filter) { value, selected ->
        value.trim() to selected.toCatalogFilter()
    }.flatMapLatest { (value, selected) -> catalogRepository.pagedSearch(value, selected) }
        .cachedIn(viewModelScope)

    fun query(value: String) { query.value = value }
    fun filter(value: SearchFilter) { filter.value = value }
    fun submit() {
        val value = query.value.trim()
        if (value.isNotEmpty()) viewModelScope.launch { historyDao.upsert(SearchHistoryEntity(value, System.currentTimeMillis())) }
    }
    fun clear() { viewModelScope.launch { historyDao.clearAll() } }
    fun removeHistoryItem(value: String) { viewModelScope.launch { historyDao.deleteByQuery(value) } }

    fun resolveSong(songId: String, onReady: (Song) -> Unit) {
        viewModelScope.launch { catalogRepository.song(songId)?.let(onReady) }
    }

    private fun SearchFilter.toCatalogFilter() = when (this) {
        SearchFilter.ALL -> CatalogFilter.ALL
        SearchFilter.SONGS -> CatalogFilter.SONGS
        SearchFilter.ARTISTS -> CatalogFilter.ARTISTS
        SearchFilter.ALBUMS -> CatalogFilter.ALBUMS
    }
}
