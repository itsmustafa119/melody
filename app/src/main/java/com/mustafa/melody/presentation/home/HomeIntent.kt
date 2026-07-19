package com.mustafa.melody.presentation.home

sealed interface HomeIntent {
    data object Retry : HomeIntent
    data object NotificationsClicked : HomeIntent
    data object SettingsClicked : HomeIntent
    data object ProfileClicked : HomeIntent
    data object LikedSongsClicked : HomeIntent
    data object RecentlyPlayedClicked : HomeIntent
    data object MyPlaylistsClicked : HomeIntent
    data object TopArtistsClicked : HomeIntent
    data class SongClicked(val songId: String) : HomeIntent
    data class SongLiked(val songId: String) : HomeIntent
    data class PlaylistClicked(val playlistId: String) : HomeIntent
    data object SeeAllRecommendationsClicked : HomeIntent
    data object SeeAllPopularSongsClicked : HomeIntent
    data object SeeAllNewestSongsClicked : HomeIntent
    data object SeeAllGlobalPlaylistsClicked : HomeIntent
    data object SeeAllLocalPlaylistsClicked : HomeIntent
}
