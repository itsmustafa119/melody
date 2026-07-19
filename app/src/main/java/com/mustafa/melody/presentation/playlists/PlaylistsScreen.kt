package com.mustafa.melody.presentation.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.EmptyState
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.MelodyTopAppBar
import com.mustafa.melody.core.designsystem.component.PlaylistCardShimmer
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun PlaylistsScreen(
    uiState: PlaylistsUiState,
    onIntent: (PlaylistsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MelodyTopAppBar(
                onProfileClick = { onIntent(PlaylistsIntent.ProfileClicked) },
                onNotificationsClick = { onIntent(PlaylistsIntent.NotificationsClicked) },
                onSettingsClick = { onIntent(PlaylistsIntent.SettingsClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = AppDimens.screenHorizontalPadding)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall),
                contentPadding = PaddingValues(vertical = AppDimens.spacingSmall)
            ) {
                items(PlaylistSection.entries.size) { index ->
                    val section = PlaylistSection.entries[index]
                    FilterChip(
                        selected = uiState.selectedSection == section,
                        onClick = { onIntent(PlaylistsIntent.SectionSelected(section)) },
                        label = {
                            Text(
                                text = when (section) {
                                    PlaylistSection.WORLD -> stringResource(R.string.world_music)
                                    PlaylistSection.LOCAL -> stringResource(R.string.local_music)
                                    PlaylistSection.USER -> stringResource(R.string.user_playlists)
                                }
                            )
                        }
                    )
                }
            }

            if (uiState.errorMessageResId != null) {
                ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(uiState.errorMessageResId),
                    onRetry = { onIntent(PlaylistsIntent.Retry) }
                )
            } else if (uiState.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = AppDimens.spacingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.gridSpacing),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.gridSpacing)
                ) {
                    items(6) { PlaylistCardShimmer() }
                }
            } else {
                EmptyState(
                    title = stringResource(R.string.no_playlists),
                    onActionClick = { /* Create playlist placeholder */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenPreview() {
    MelodyTheme {
        PlaylistsScreen(
            uiState = PlaylistsUiState(),
            onIntent = {}
        )
    }
}
