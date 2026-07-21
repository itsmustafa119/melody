package com.mustafa.melody.presentation.downloads

sealed interface DownloadsIntent {
    data class SortSelected(val sortOption: DownloadSortOption) : DownloadsIntent
    data object NotificationsClicked : DownloadsIntent
    data object SettingsClicked : DownloadsIntent
    data object ProfileClicked : DownloadsIntent
    data object Retry : DownloadsIntent
    data class SongClicked(val songId: String) : DownloadsIntent
    data class RemoveSong(val songId: String, val localPath: String?) : DownloadsIntent
}
