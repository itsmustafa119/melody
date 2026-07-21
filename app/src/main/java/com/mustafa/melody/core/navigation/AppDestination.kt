package com.mustafa.melody.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.mustafa.melody.R

enum class AppDestination(
    val route: String,
    @param:StringRes val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = "home",
        labelResId = R.string.home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    SEARCH(
        route = "search",
        labelResId = R.string.search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    DOWNLOADS(
        route = "downloads",
        labelResId = R.string.downloads,
        selectedIcon = Icons.Filled.Download,
        unselectedIcon = Icons.Outlined.Download
    ),
    PLAYLISTS(
        route = "playlists",
        labelResId = R.string.playlists,
        selectedIcon = Icons.Filled.LibraryMusic,
        unselectedIcon = Icons.Outlined.LibraryMusic
    ),
    PROFILE(
        route = "profile",
        labelResId = R.string.profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

val topLevelDestinations = AppDestination.entries

object AppRoute {
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val NOW_PLAYING = "now_playing"
    const val AUTH = "auth"
    const val SOCIAL = "social"
    const val CHAT = "chat"
    const val LIKED = "liked"
    const val RECENT = "recent"
    const val PLAYLIST_DETAIL = "playlist/{playlistId}"
    fun playlistDetail(playlistId: String) = "playlist/$playlistId"
}
