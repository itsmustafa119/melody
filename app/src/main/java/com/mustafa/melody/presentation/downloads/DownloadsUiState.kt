package com.mustafa.melody.presentation.downloads

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val selectedSortOption: DownloadSortOption = DownloadSortOption.RECENT,
    val downloadedSongCount: Int = 0,
    val errorMessageResId: Int? = null
)
