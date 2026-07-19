package com.mustafa.melody.presentation.downloads

sealed interface DownloadsIntent {
    data class SortSelected(val sortOption: DownloadSortOption) : DownloadsIntent
    data object NotificationsClicked : DownloadsIntent
    data object SettingsClicked : DownloadsIntent
    data object ProfileClicked : DownloadsIntent
    data object Retry : DownloadsIntent
}
