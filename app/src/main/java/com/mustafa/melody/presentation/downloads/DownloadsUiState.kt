package com.mustafa.melody.presentation.downloads

import com.mustafa.melody.domain.model.DownloadedSong

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val selectedSortOption: DownloadSortOption = DownloadSortOption.RECENT,
    val downloadedSongCount: Int = 0,
    val errorMessageResId: Int? = null,
    val songs: List<DownloadedSong> = emptyList(),
)
