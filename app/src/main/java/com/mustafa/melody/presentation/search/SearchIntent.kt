package com.mustafa.melody.presentation.search

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class FilterSelected(val filter: SearchFilter) : SearchIntent
    data class HistoryItemClicked(val query: String) : SearchIntent
    data object ClearHistoryClicked : SearchIntent
    data object NotificationsClicked : SearchIntent
    data object SettingsClicked : SearchIntent
    data object ProfileClicked : SearchIntent
    data object Retry : SearchIntent
}
