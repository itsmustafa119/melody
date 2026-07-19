package com.mustafa.melody.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mustafa.melody.presentation.downloads.DownloadsScreen
import com.mustafa.melody.presentation.home.HomeScreen
import com.mustafa.melody.presentation.playlists.PlaylistsScreen
import com.mustafa.melody.presentation.profile.ProfileScreen
import com.mustafa.melody.presentation.search.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME.route,
        modifier = modifier
    ) {
        composable(AppDestination.HOME.route) {
            HomeScreen()
        }
        composable(AppDestination.SEARCH.route) {
            SearchScreen()
        }
        composable(AppDestination.DOWNLOADS.route) {
            DownloadsScreen()
        }
        composable(AppDestination.PLAYLISTS.route) {
            PlaylistsScreen()
        }
        composable(AppDestination.PROFILE.route) {
            ProfileScreen()
        }
    }
}
