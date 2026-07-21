package com.mustafa.melody.presentation.playlists

import com.mustafa.melody.domain.model.MusicPlaylist

data class PlaylistsUiState(
    val isLoading: Boolean = true,
    val selectedSection: PlaylistSection = PlaylistSection.WORLD,
    val errorMessageResId: Int? = null,
    val playlists: List<MusicPlaylist> = emptyList(),
)
