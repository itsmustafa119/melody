package com.mustafa.melody.presentation.search

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val searchHistory: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
