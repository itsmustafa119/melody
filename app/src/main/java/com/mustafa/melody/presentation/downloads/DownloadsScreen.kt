package com.mustafa.melody.presentation.downloads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mustafa.melody.R
import com.mustafa.melody.core.designsystem.component.EmptyState
import com.mustafa.melody.core.designsystem.component.ErrorState
import com.mustafa.melody.core.designsystem.component.MelodyTopAppBar
import com.mustafa.melody.core.designsystem.component.SectionHeader
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun DownloadsScreen(
    uiState: DownloadsUiState,
    onIntent: (DownloadsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
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
            SectionHeader(
                title = stringResource(R.string.downloaded_songs),
                actionLabel = stringResource(R.string.sort),
                onActionClick = { /* Show sort menu placeholder */ }
            )

            if (uiState.errorMessageResId != null) {
                ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(uiState.errorMessageResId),
                    onRetry = { onIntent(DownloadsIntent.Retry) }
                )
            } else if (uiState.isLoading) {
                repeat(5) { SongCardShimmer() }
            } else if (uiState.downloadedSongCount == 0) {
                EmptyState(
                    title = stringResource(R.string.no_downloads),
                    description = stringResource(R.string.no_downloads_description)
                )
            } else {
                // List of downloaded songs placeholder
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
