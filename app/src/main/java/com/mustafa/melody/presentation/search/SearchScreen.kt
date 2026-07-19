package com.mustafa.melody.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.mustafa.melody.core.designsystem.component.SectionHeader
import com.mustafa.melody.core.designsystem.component.SongCardShimmer
import com.mustafa.melody.core.designsystem.theme.AppDimens
import com.mustafa.melody.core.designsystem.theme.MelodyTheme

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MelodyTopAppBar(
                onProfileClick = { onIntent(SearchIntent.ProfileClicked) },
                onNotificationsClick = { onIntent(SearchIntent.NotificationsClicked) },
                onSettingsClick = { onIntent(SearchIntent.SettingsClicked) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = AppDimens.spacingMedium)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimens.spacingSmall),
                placeholder = { Text(stringResource(R.string.search_music)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall),
                contentPadding = PaddingValues(vertical = AppDimens.spacingSmall)
            ) {
                items(SearchFilter.entries) { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { onIntent(SearchIntent.FilterSelected(filter)) },
                        label = {
                            Text(
                                text = when (filter) {
                                    SearchFilter.ALL -> stringResource(R.string.see_all)
                                    SearchFilter.SONGS -> stringResource(R.string.songs)
                                    SearchFilter.ARTISTS -> stringResource(R.string.artists)
                                    SearchFilter.ALBUMS -> stringResource(R.string.albums)
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
                    onRetry = { onIntent(SearchIntent.Retry) }
                )
            } else if (uiState.isLoading) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimens.spacingSmall)) {
                    repeat(5) { SongCardShimmer() }
                }
            } else if (uiState.query.isEmpty()) {
                if (uiState.searchHistory.isEmpty()) {
                    EmptyState(
                        title = stringResource(R.string.start_searching),
                        icon = Icons.Default.Search
                    )
                } else {
                    SectionHeader(
                        title = stringResource(R.string.search_history),
                        actionLabel = stringResource(R.string.clear_history),
                        onActionClick = { onIntent(SearchIntent.ClearHistoryClicked) }
                    )
                    LazyColumn {
                        items(uiState.searchHistory) { historyItem ->
                            // History Item placeholder
                        }
                    }
                }
            } else {
                // Results area placeholder
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MelodyTheme {
        SearchScreen(
            uiState = SearchUiState(),
            onIntent = {}
        )
    }
}
