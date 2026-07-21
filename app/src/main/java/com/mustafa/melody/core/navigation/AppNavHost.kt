package com.mustafa.melody.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mustafa.melody.R
import com.mustafa.melody.presentation.app.AppIntent
import com.mustafa.melody.presentation.app.AppUiState
import com.mustafa.melody.presentation.downloads.DownloadSortOption
import com.mustafa.melody.presentation.downloads.DownloadsIntent
import com.mustafa.melody.presentation.downloads.DownloadsScreen
import com.mustafa.melody.presentation.downloads.DownloadsUiState
import com.mustafa.melody.presentation.home.HomeIntent
import com.mustafa.melody.presentation.home.HomeScreen
import com.mustafa.melody.presentation.home.HomeUiState
import com.mustafa.melody.presentation.notifications.NotificationsScreen
import com.mustafa.melody.presentation.playlists.PlaylistSection
import com.mustafa.melody.presentation.playlists.PlaylistsIntent
import com.mustafa.melody.presentation.playlists.PlaylistsScreen
import com.mustafa.melody.presentation.playlists.PlaylistsUiState
import com.mustafa.melody.presentation.profile.ProfileIntent
import com.mustafa.melody.presentation.profile.ProfileScreen
import com.mustafa.melody.presentation.profile.ProfileUiState
import com.mustafa.melody.presentation.search.SearchFilter
import com.mustafa.melody.presentation.search.SearchIntent
import com.mustafa.melody.presentation.search.SearchScreen
import com.mustafa.melody.presentation.search.SearchUiState
import com.mustafa.melody.presentation.settings.SettingsScreen
import com.mustafa.melody.domain.model.PlaylistKind
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafa.melody.player.PlaybackStore
import com.mustafa.melody.presentation.player.NowPlayingScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mustafa.melody.presentation.downloads.DownloadsViewModel
import com.mustafa.melody.download.SongDownloadWorker
import android.net.Uri
import com.mustafa.melody.presentation.auth.AuthViewModel
import com.mustafa.melody.presentation.auth.AuthScreen
import com.mustafa.melody.presentation.social.ChatViewModel
import com.mustafa.melody.presentation.social.ChatScreen
import com.mustafa.melody.presentation.social.SocialScreen
import com.mustafa.melody.presentation.social.SocialViewModel
import com.mustafa.melody.presentation.search.SearchViewModel
import com.mustafa.melody.presentation.library.LibraryViewModel
import com.mustafa.melody.presentation.library.SongCollectionScreen
import com.mustafa.melody.presentation.home.HomeViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafa.melody.presentation.playlists.PlaylistsViewModel
import com.mustafa.melody.presentation.playlists.PlaylistsEffect
import com.mustafa.melody.presentation.playlists.PlaylistDetailsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appUiState: AppUiState,
    onAppIntent: (AppIntent) -> Unit,
    onShowMessage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playbackState by PlaybackStore.state.collectAsStateWithLifecycle()
    var playbackSpeed by rememberSaveable { mutableFloatStateOf(1f) }
    val downloadsViewModel: DownloadsViewModel = viewModel()
    val downloadedSongs by downloadsViewModel.songs.collectAsStateWithLifecycle()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val chatViewModel: ChatViewModel = viewModel()
    val chatMessages by chatViewModel.messages.collectAsStateWithLifecycle()
    val isOtherUserTyping by chatViewModel.isOtherUserTyping.collectAsStateWithLifecycle()
    val searchViewModel: SearchViewModel = viewModel()
    val persistedSearchState by searchViewModel.state.collectAsStateWithLifecycle()
    val pagedSearchResults = searchViewModel.pagedResults.collectAsLazyPagingItems()
    val homeViewModel: HomeViewModel = viewModel()
    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val playlistsViewModel: PlaylistsViewModel = viewModel()
    val playlistsState by playlistsViewModel.state.collectAsStateWithLifecycle()
    val playlistDetailsState by playlistsViewModel.details.collectAsStateWithLifecycle()
    val socialViewModel: SocialViewModel = viewModel()
    val socialState by socialViewModel.state.collectAsStateWithLifecycle()
    val libraryViewModel: LibraryViewModel = viewModel()
    val likedSongs by libraryViewModel.likedSongs.collectAsStateWithLifecycle()
    val recentlyPlayed by libraryViewModel.recentlyPlayed.collectAsStateWithLifecycle()
    var selectedSocialUserId by rememberSaveable { mutableStateOf("") }
    var selectedSocialUserName by rememberSaveable { mutableStateOf("") }
    var downloadSortName by rememberSaveable { mutableStateOf(DownloadSortOption.RECENT.name) }
    val likedSongIds = likedSongs.map { it.id }.toSet()

    LaunchedEffect(playlistsViewModel) {
        playlistsViewModel.effects.collect { effect ->
            when (effect) {
                PlaylistsEffect.PlaylistCreated -> onShowMessage(R.string.playlist_created)
                is PlaylistsEffect.ShowError -> onShowMessage(effect.messageResId)
            }
        }
    }

    fun openSettings() {
        navController.navigate(AppRoute.SETTINGS) { launchSingleTop = true }
    }

    fun openNotifications() {
        navController.navigate(AppRoute.NOTIFICATIONS) { launchSingleTop = true }
    }

    fun openProfile() {
        navController.navigateToTopLevelDestination(AppDestination.PROFILE)
    }

    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME.route,
        modifier = modifier,
    ) {
        composable(AppDestination.HOME.route) {
            HomeScreen(
                uiState = homeState.copy(
                    recommendations = homeState.recommendations.map { it.copy(isLiked = it.id in likedSongIds) },
                    popularSongs = homeState.popularSongs.map { it.copy(isLiked = it.id in likedSongIds) },
                    newestSongs = homeState.newestSongs.map { it.copy(isLiked = it.id in likedSongIds) },
                ),
                onIntent = { intent ->
                    when (intent) {
                        HomeIntent.ProfileClicked -> openProfile()
                        HomeIntent.NotificationsClicked -> openNotifications()
                        HomeIntent.SettingsClicked -> openSettings()
                        HomeIntent.MyPlaylistsClicked,
                        HomeIntent.SeeAllGlobalPlaylistsClicked,
                        HomeIntent.SeeAllLocalPlaylistsClicked ->
                            navController.navigateToTopLevelDestination(AppDestination.PLAYLISTS)

                        HomeIntent.TopArtistsClicked,
                        HomeIntent.SeeAllRecommendationsClicked,
                        HomeIntent.SeeAllPopularSongsClicked,
                        HomeIntent.SeeAllNewestSongsClicked ->
                            navController.navigateToTopLevelDestination(AppDestination.SEARCH)

                        HomeIntent.LikedSongsClicked -> navController.navigate(AppRoute.LIKED) { launchSingleTop = true }
                        HomeIntent.RecentlyPlayedClicked -> navController.navigate(AppRoute.RECENT) { launchSingleTop = true }
                        HomeIntent.Retry -> homeViewModel.onIntent(intent)
                        is HomeIntent.PlaylistClicked -> navController.navigate(AppRoute.playlistDetail(intent.playlistId))
                        is HomeIntent.SongClicked -> homeViewModel.resolveSong(intent.songId) { PlaybackStore.play(context, it) }
                        is HomeIntent.SongLiked -> {
                            libraryViewModel.toggle(intent.songId)
                        }
                    }
                },
            )
        }
        composable(AppDestination.SEARCH.route) {
            SearchScreen(
                uiState = persistedSearchState,
                pagedSongs = pagedSearchResults,
                likedSongIds = likedSongIds,
                onIntent = { intent ->
                    when (intent) {
                        is SearchIntent.QueryChanged -> searchViewModel.query(intent.query)
                        is SearchIntent.FilterSelected -> searchViewModel.filter(intent.filter)
                        is SearchIntent.HistoryItemClicked -> searchViewModel.query(intent.query)
                        SearchIntent.SearchSubmitted -> searchViewModel.submit()
                        SearchIntent.ClearHistoryClicked -> searchViewModel.clear()
                        is SearchIntent.RemoveHistoryItem -> searchViewModel.removeHistoryItem(intent.query)
                        SearchIntent.ProfileClicked -> openProfile()
                        SearchIntent.NotificationsClicked -> openNotifications()
                        SearchIntent.SettingsClicked -> openSettings()
                        SearchIntent.Retry -> pagedSearchResults.retry()
                        is SearchIntent.SongClicked -> searchViewModel.resolveSong(intent.songId) { PlaybackStore.play(context, it) }
                        is SearchIntent.SongLiked -> {
                            libraryViewModel.toggle(intent.songId)
                        }
                    }
                },
            )
        }
        composable(AppDestination.DOWNLOADS.route) {
            DownloadsScreen(
                uiState = DownloadsUiState(
                    selectedSortOption = DownloadSortOption.valueOf(downloadSortName),
                    downloadedSongCount = downloadedSongs.size,
                    songs = when (DownloadSortOption.valueOf(downloadSortName)) {
                        DownloadSortOption.RECENT -> downloadedSongs.sortedByDescending { it.downloadedAt }
                        DownloadSortOption.TITLE -> downloadedSongs.sortedBy { it.title.lowercase() }
                        DownloadSortOption.ARTIST -> downloadedSongs.sortedBy { it.artistName.lowercase() }
                    },
                ),
                onIntent = { intent ->
                    when (intent) {
                        is DownloadsIntent.SortSelected -> downloadSortName = intent.sortOption.name
                        DownloadsIntent.ProfileClicked -> openProfile()
                        DownloadsIntent.NotificationsClicked -> openNotifications()
                        DownloadsIntent.SettingsClicked -> openSettings()
                        DownloadsIntent.Retry -> onShowMessage(R.string.feature_requires_music_data)
                        is DownloadsIntent.RemoveSong -> downloadsViewModel.remove(intent.songId, intent.localPath)
                        is DownloadsIntent.SongClicked -> downloadedSongs.find { it.songId == intent.songId }?.let { downloaded ->
                            PlaybackStore.play(
                                context,
                                com.mustafa.melody.domain.model.Song(
                                    id = downloaded.songId,
                                    title = downloaded.title,
                                    artistName = downloaded.artistName,
                                    coverImageUrl = downloaded.coverImageUrl.orEmpty(),
                                    audioUrl = downloaded.localFilePath?.let { Uri.fromFile(java.io.File(it)).toString() }
                                        ?: downloaded.remoteAudioUrl.orEmpty(),
                                    album = "Downloads",
                                ),
                            )
                        }
                    }
                },
            )
        }
        composable(AppDestination.PLAYLISTS.route) {
            PlaylistsScreen(
                uiState = playlistsState,
                onIntent = { intent ->
                    when (intent) {
                        is PlaylistsIntent.SectionSelected,
                        is PlaylistsIntent.CreatePlaylist,
                        PlaylistsIntent.CreatePlaylistClicked,
                        PlaylistsIntent.Retry -> playlistsViewModel.onIntent(intent)
                        PlaylistsIntent.ProfileClicked -> openProfile()
                        PlaylistsIntent.NotificationsClicked -> openNotifications()
                        PlaylistsIntent.SettingsClicked -> openSettings()
                        is PlaylistsIntent.PlaylistClicked -> navController.navigate(AppRoute.playlistDetail(intent.playlistId))
                    }
                },
            )
        }
        composable(AppDestination.PROFILE.route) {
            ProfileScreen(
                uiState = ProfileUiState(
                    displayName = authState.displayName,
                    isPremium = appUiState.isPremium,
                    avatarUrl = authState.avatarUrl,
                ),
                onAvatarSelected = { authViewModel.uploadAvatar(context, it) },
                onIntent = { intent ->
                    when (intent) {
                        ProfileIntent.SettingsClicked -> openSettings()
                        ProfileIntent.NotificationsClicked -> openNotifications()
                        ProfileIntent.UpgradeClicked -> {
                            onAppIntent(AppIntent.ChangePremiumStatus(true))
                            onShowMessage(R.string.premium_enabled)
                        }
                        ProfileIntent.RenewSubscriptionClicked ->
                            onShowMessage(R.string.premium_already_active)
                        ProfileIntent.ChangeAvatarClicked ->
                            onShowMessage(R.string.avatar_requires_account)
                        ProfileIntent.Retry -> onShowMessage(R.string.try_again_later)
                        ProfileIntent.AccountClicked -> navController.navigate(AppRoute.AUTH) { launchSingleTop = true }
                        ProfileIntent.SocialClicked -> navController.navigate(AppRoute.SOCIAL) { launchSingleTop = true }
                    }
                },
            )
        }
        composable(AppRoute.SETTINGS) {
            SettingsScreen(
                uiState = appUiState,
                onIntent = onAppIntent,
                onBackClick = navController::popBackStack,
                isSignedIn = authState.isSignedIn,
                onOpenAccount = { navController.navigate(AppRoute.AUTH) { launchSingleTop = true } },
                onSignOut = authViewModel::signOut,
            )
        }
        composable(AppRoute.NOTIFICATIONS) {
            NotificationsScreen(onBackClick = navController::popBackStack)
        }
        composable(AppRoute.NOW_PLAYING) {
            NowPlayingScreen(
                state = playbackState,
                speed = playbackSpeed,
                onBack = navController::popBackStack,
                onToggle = { PlaybackStore.toggle(context) },
                onPrevious = { PlaybackStore.previous(context) },
                onNext = { PlaybackStore.next(context) },
                onSeek = { PlaybackStore.seekTo(context, it) },
                onSpeed = {
                    playbackSpeed = it
                    PlaybackStore.setSpeed(context, it)
                },
                onSleepTimer = {
                    PlaybackStore.startSleepTimer(context)
                    onShowMessage(R.string.sleep_timer_started)
                },
                onDownload = {
                    val song = playbackState.song
                    if (!appUiState.isPremium) onShowMessage(R.string.premium_required_download)
                    else if (song != null) {
                        SongDownloadWorker.enqueue(context, song)
                        onShowMessage(R.string.download_started)
                    }
                },
            )
        }
        composable(AppRoute.AUTH) {
            AuthScreen(
                state = authState,
                onEmail = authViewModel::email,
                onPassword = authViewModel::password,
                onDisplayName = authViewModel::displayName,
                onSaveProfile = authViewModel::saveProfile,
                onSignIn = authViewModel::signIn,
                onSignUp = authViewModel::signUp,
                onSignOut = authViewModel::signOut,
                onBack = navController::popBackStack,
            )
        }
        composable(AppRoute.SOCIAL) {
            SocialScreen(
                state = socialState,
                onBack = navController::popBackStack,
                onQuery = socialViewModel::query,
                onToggleFollow = socialViewModel::toggleFollow,
                onViewPlaylists = socialViewModel::viewPublicPlaylists,
                onPlaylist = { navController.navigate(AppRoute.playlistDetail(it)) },
                onRetry = socialViewModel::retry,
                onChat = { user ->
                    selectedSocialUserId = user.id
                    selectedSocialUserName = user.displayName
                    chatViewModel.open(user.id)
                    navController.navigate(AppRoute.CHAT) { launchSingleTop = true }
                },
            )
        }
        composable(AppRoute.CHAT) {
            ChatScreen(
                userId = selectedSocialUserId,
                userName = selectedSocialUserName,
                messages = chatMessages,
                isOtherUserTyping = isOtherUserTyping,
                currentSong = playbackState.song,
                onBack = navController::popBackStack,
                onSend = chatViewModel::send,
                onShareSong = { chatViewModel.send(null, it) },
                onTypingChanged = chatViewModel::onTypingChanged,
                onPlaySharedSong = { songId -> searchViewModel.resolveSong(songId) { PlaybackStore.play(context, it) } },
            )
        }
        composable(AppRoute.LIKED) {
            SongCollectionScreen(
                title = androidx.compose.ui.res.stringResource(R.string.liked_songs),
                songs = likedSongs,
                onBack = navController::popBackStack,
                onPlayQueue = { songs, index -> PlaybackStore.playQueue(context, songs, index) },
                onRemove = { libraryViewModel.toggle(it.id) },
            )
        }
        composable(AppRoute.RECENT) {
            SongCollectionScreen(
                title = androidx.compose.ui.res.stringResource(R.string.recently_played),
                songs = recentlyPlayed,
                onBack = navController::popBackStack,
                onPlayQueue = { songs, index -> PlaybackStore.playQueue(context, songs, index) },
            )
        }
        composable(AppRoute.PLAYLIST_DETAIL) { entry ->
            val playlistId = entry.arguments?.getString("playlistId").orEmpty()
            LaunchedEffect(playlistId) { playlistsViewModel.openPlaylist(playlistId) }
            PlaylistDetailsScreen(
                state = playlistDetailsState,
                onBack = navController::popBackStack,
                onPlayQueue = { songs, index -> PlaybackStore.playQueue(context, songs, index) },
                likedSongIds = likedSongIds,
                onToggleLike = libraryViewModel::toggle,
                onRetry = { playlistsViewModel.openPlaylist(playlistId) },
            )
        }
    }
}
