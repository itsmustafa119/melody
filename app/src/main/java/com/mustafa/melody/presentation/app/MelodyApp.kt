package com.mustafa.melody.presentation.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafa.melody.player.MiniPlayer
import com.mustafa.melody.player.PlaybackStore
import com.mustafa.melody.core.navigation.AppRoute
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mustafa.melody.core.navigation.AppNavHost
import com.mustafa.melody.core.navigation.navigateToTopLevelDestination
import com.mustafa.melody.core.navigation.topLevelDestinations
import kotlinx.coroutines.launch

@Composable
fun MelodyApp(
    appUiState: AppUiState,
    onAppIntent: (AppIntent) -> Unit,
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val resources = LocalResources.current
    val context = LocalContext.current
    val playbackState by PlaybackStore.state.collectAsStateWithLifecycle()
    val showMessage: (Int) -> Unit = { messageResId ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(resources.getString(messageResId))
        }
    }
    val showBottomBar = currentDestination?.route in topLevelDestinations.map { it.route }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column {
                if (playbackState.song != null && currentDestination?.route != AppRoute.NOW_PLAYING) {
                    MiniPlayer(
                        state = playbackState,
                        onClick = { navController.navigate(AppRoute.NOW_PLAYING) { launchSingleTop = true } },
                        onToggle = { PlaybackStore.toggle(context) },
                    )
                }
                if (showBottomBar) {
                    NavigationBar {
                        topLevelDestinations.forEach { destination ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == destination.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToTopLevelDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = stringResource(destination.labelResId)
                                )
                            },
                            label = {
                                Text(text = stringResource(destination.labelResId))
                            }
                        )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            appUiState = appUiState,
            onAppIntent = onAppIntent,
            onShowMessage = showMessage,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
