package com.mustafa.melody.presentation.downloads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.EmptyState
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.MelodyTopAppBar
import com.mustafa.melody.core.designsystem.component.SectionHeader
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.component.SongCard
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun DownloadsScreen(
    uiState: DownloadsUiState,
    onIntent: (DownloadsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            MelodyTopAppBar(
                onProfileClick = { onIntent(DownloadsIntent.ProfileClicked) },
                onNotificationsClick = { onIntent(DownloadsIntent.NotificationsClicked) },
                onSettingsClick = { onIntent(DownloadsIntent.SettingsClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = AppDimens.screenHorizontalPadding)
        ) {
            Box {
                SectionHeader(
                    title = stringResource(R.string.downloaded_songs),
                    actionLabel = stringResource(
                        R.string.sort_by,
                        stringResource(uiState.selectedSortOption.labelResId),
                    ),
                    onActionClick = { showSortMenu = true },
                )
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                ) {
                    DownloadSortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(option.labelResId)) },
                            onClick = {
                                showSortMenu = false
                                onIntent(DownloadsIntent.SortSelected(option))
                            },
                        )
                    }
                }
            }

            if (uiState.errorMessageResId != null) {
                ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(uiState.errorMessageResId),
                    onRetry = { onIntent(DownloadsIntent.Retry) }
                )
            } else if (uiState.isLoading) {
                repeat(5) { SongCardShimmer() }
            } else if (uiState.songs.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.no_downloads),
                    description = stringResource(R.string.no_downloads_description)
                )
            } else {
                LazyColumn {
                    items(uiState.songs, key = { it.songId }) { song ->
                        val dismissState = rememberSwipeToDismissBoxState()
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                onIntent(DownloadsIntent.RemoveSong(song.songId, song.localFilePath))
                            }
                        }
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer),
                                    contentAlignment = Alignment.CenterEnd,
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        stringResource(R.string.remove_download),
                                        Modifier.padding(AppDimens.spacingLarge),
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                    )
                                }
                            },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SongCard(
                                    title = song.title,
                                    artistName = song.artistName,
                                    coverImageUrl = song.coverImageUrl,
                                    modifier = Modifier.weight(1f),
                                    showLikeAction = false,
                                    onClick = { onIntent(DownloadsIntent.SongClicked(song.songId)) },
                                )
                                IconButton(onClick = { onIntent(DownloadsIntent.RemoveSong(song.songId, song.localFilePath)) }) {
                                    Icon(Icons.Default.Delete, stringResource(R.string.remove_download))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadsScreenPreview() {
    MelodyTheme {
        DownloadsScreen(
            uiState = DownloadsUiState(),
            onIntent = {}
        )
    }
}
