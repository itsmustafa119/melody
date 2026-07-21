package com.mustafa.melody.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.MelodyTopAppBar
import com.mustafa.melody.core.designsystem.component.PlaylistCardShimmer
import com.mustafa.melody.core.designsystem.component.QuickActionItem
import com.mustafa.melody.core.designsystem.component.SectionHeader
import com.mustafa.melody.core.designsystem.component.ShimmerBox
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.component.SongCard
import com.mustafa.melody.core.designsystem.component.PlaylistCard
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MelodyTopAppBar(
                onProfileClick = { onIntent(HomeIntent.ProfileClicked) },
                onNotificationsClick = { onIntent(HomeIntent.NotificationsClicked) },
                onSettingsClick = { onIntent(HomeIntent.SettingsClicked) }
            )
        }
    ) { innerPadding ->
        if (uiState.errorMessageResId != null) {
            ErrorState(
                title = stringResource(R.string.something_went_wrong),
                description = stringResource(uiState.errorMessageResId),
                onRetry = { onIntent(HomeIntent.Retry) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(AppDimens.spacingMedium),
                verticalArrangement = Arrangement.spacedBy(AppDimens.sectionSpacing)
            ) {
                // Recommendation Carousel
                item {
                    SectionHeader(
                        title = stringResource(R.string.recommendations_for_you),
                        actionLabel = stringResource(R.string.see_all),
                        onActionClick = { onIntent(HomeIntent.SeeAllRecommendationsClicked) }
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
                        contentPadding = PaddingValues(vertical = AppDimens.spacingSmall)
                    ) {
                        if (uiState.isLoading) {
                            items(5) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(AppDimens.cardWidthLarge)
                                        .height(AppDimens.carouselCardHeight)
                                )
                            }
                        } else {
                            items(uiState.recommendations, key = { it.id }) { song ->
                                PlaylistCard(
                                    title = song.title,
                                    subtitle = song.artistName,
                                    coverImageUrl = song.coverImageUrl,
                                    onClick = { onIntent(HomeIntent.SongClicked(song.id)) },
                                )
                            }
                        }
                    }
                }

                // Quick Actions
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium)
                        ) {
                            QuickActionItem(
                                title = stringResource(R.string.liked_songs),
                                icon = Icons.Default.ThumbUp,
                                onClick = { onIntent(HomeIntent.LikedSongsClicked) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionItem(
                                title = stringResource(R.string.recently_played),
                                icon = Icons.Default.History,
                                onClick = { onIntent(HomeIntent.RecentlyPlayedClicked) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium)
                        ) {
                            QuickActionItem(
                                title = stringResource(R.string.my_playlists),
                                icon = Icons.Default.LibraryMusic,
                                onClick = { onIntent(HomeIntent.MyPlaylistsClicked) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionItem(
                                title = stringResource(R.string.top_artists),
                                icon = Icons.Default.Star,
                                onClick = { onIntent(HomeIntent.TopArtistsClicked) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Popular Songs
                item {
                    SectionHeader(
                        title = stringResource(R.string.popular_songs),
                        actionLabel = stringResource(R.string.see_all),
                        onActionClick = { onIntent(HomeIntent.SeeAllPopularSongsClicked) }
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                        if (uiState.isLoading) {
                            repeat(3) {
                                SongCardShimmer()
                            }
                        } else {
                            uiState.popularSongs.take(5).forEach { song ->
                                SongCard(
                                    title = song.title,
                                    artistName = song.artistName,
                                    coverImageUrl = song.coverImageUrl,
                                    isLiked = song.isLiked,
                                    onClick = { onIntent(HomeIntent.SongClicked(song.id)) },
                                    onLikeClick = { onIntent(HomeIntent.SongLiked(song.id)) },
                                )
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = stringResource(R.string.newest_songs),
                        actionLabel = stringResource(R.string.see_all),
                        onActionClick = { onIntent(HomeIntent.SeeAllNewestSongsClicked) },
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                        uiState.newestSongs.take(4).forEach { song ->
                            SongCard(
                                title = song.title,
                                artistName = song.artistName,
                                coverImageUrl = song.coverImageUrl,
                                onClick = { onIntent(HomeIntent.SongClicked(song.id)) },
                                onLikeClick = { onIntent(HomeIntent.SongLiked(song.id)) },
                            )
                        }
                    }
                }

                // Global Playlists
                item {
                    SectionHeader(
                        title = stringResource(R.string.global_playlists),
                        actionLabel = stringResource(R.string.see_all),
                        onActionClick = { onIntent(HomeIntent.SeeAllGlobalPlaylistsClicked) }
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
                        contentPadding = PaddingValues(vertical = AppDimens.spacingSmall)
                    ) {
                        if (uiState.isLoading) {
                            items(5) {
                                PlaylistCardShimmer()
                            }
                        } else {
                            items(uiState.globalPlaylists, key = { it.id }) { playlist ->
                                PlaylistCard(
                                    title = playlist.title,
                                    subtitle = playlist.subtitle,
                                    coverImageUrl = playlist.coverImageUrl,
                                    onClick = { onIntent(HomeIntent.PlaylistClicked(playlist.id)) },
                                )
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = stringResource(R.string.local_playlists),
                        actionLabel = stringResource(R.string.see_all),
                        onActionClick = { onIntent(HomeIntent.SeeAllLocalPlaylistsClicked) },
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium)) {
                        items(uiState.localPlaylists, key = { it.id }) { playlist ->
                            PlaylistCard(
                                title = playlist.title,
                                subtitle = playlist.subtitle,
                                coverImageUrl = playlist.coverImageUrl,
                                onClick = { onIntent(HomeIntent.PlaylistClicked(playlist.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MelodyTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onIntent = {}
        )
    }
}
