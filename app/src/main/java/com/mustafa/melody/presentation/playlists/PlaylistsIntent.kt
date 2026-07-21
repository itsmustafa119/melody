package com.mustafa.melody.presentation.playlists

sealed interface PlaylistsIntent {
    data class SectionSelected(val section: PlaylistSection) : PlaylistsIntent
    data object CreatePlaylistClicked : PlaylistsIntent
    data class CreatePlaylist(val title: String) : PlaylistsIntent
    data object NotificationsClicked : PlaylistsIntent
    data object SettingsClicked : PlaylistsIntent
    data object ProfileClicked : PlaylistsIntent
    data object Retry : PlaylistsIntent
    data class PlaylistClicked(val playlistId: String) : PlaylistsIntent
}
