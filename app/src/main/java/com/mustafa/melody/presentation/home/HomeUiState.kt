package com.mustafa.melody.presentation.home

import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.Song

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessageResId: Int? = null,
    val recommendations: List<Song> = emptyList(),
    val popularSongs: List<Song> = emptyList(),
    val newestSongs: List<Song> = emptyList(),
    val globalPlaylists: List<MusicPlaylist> = emptyList(),
    val localPlaylists: List<MusicPlaylist> = emptyList(),
    val usingOfflineFallback: Boolean = false,
)
