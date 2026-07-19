package com.mustafa.melody.presentation.playlists

sealed interface PlaylistsIntent {
    data class SectionSelected(val section: PlaylistSection) : PlaylistsIntent
    data object NotificationsClicked : PlaylistsIntent
    data object SettingsClicked : PlaylistsIntent
    data object ProfileClicked : PlaylistsIntent
    data object Retry : PlaylistsIntent
}
