package com.mustafa.melody.presentation.search

import com.mustafa.melody.domain.model.Song

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val searchHistory: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val results: List<Song> = emptyList(),
)
