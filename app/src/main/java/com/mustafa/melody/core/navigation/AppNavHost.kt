package com.mustafa.melody.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mustafa.melody.presentation.downloads.DownloadsScreen
import com.mustafa.melody.presentation.downloads.DownloadsUiState
import com.mustafa.melody.presentation.home.HomeScreen
import com.mustafa.melody.presentation.home.HomeUiState
import com.mustafa.melody.presentation.playlists.PlaylistsScreen
import com.mustafa.melody.presentation.playlists.PlaylistsUiState
import com.mustafa.melody.presentation.profile.ProfileScreen
import com.mustafa.melody.presentation.profile.ProfileUiState
import com.mustafa.melody.presentation.search.SearchScreen
import com.mustafa.melody.presentation.search.SearchUiState

@Composable
fun AppNavHost(
    navController: NavHostController,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME.route,
        modifier = modifier
    ) {
        composable(AppDestination.HOME.route) {
            // TODO: Replace with real state and ViewModel
            HomeScreen(
                uiState = HomeUiState(),
                onIntent = { /* Handle Home Intent */ }
            )
        }
        composable(AppDestination.SEARCH.route) {
            // TODO: Replace with real state and ViewModel
            SearchScreen(
                uiState = SearchUiState(),
                onIntent = { /* Handle Search Intent */ }
            )
        }
        composable(AppDestination.DOWNLOADS.route) {
            // TODO: Replace with real state and ViewModel
            DownloadsScreen(
                uiState = DownloadsUiState(),
                onIntent = { /* Handle Downloads Intent */ }
            )
        }
        composable(AppDestination.PLAYLISTS.route) {
            // TODO: Replace with real state and ViewModel
            PlaylistsScreen(
                uiState = PlaylistsUiState(),
                onIntent = { /* Handle Playlists Intent */ }
            )
        }
        composable(AppDestination.PROFILE.route) {
            // TODO: Replace with real state and ViewModel
            ProfileScreen(
                uiState = ProfileUiState(isPremium = isPremium),
                onIntent = { /* Handle Profile Intent */ }
            )
        }
    }
}
