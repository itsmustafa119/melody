package com.mustafa.melody.presentation.playlists

data class PlaylistsUiState(
    val isLoading: Boolean = true,
    val selectedSection: PlaylistSection = PlaylistSection.WORLD,
    val errorMessageResId: Int? = null
)
