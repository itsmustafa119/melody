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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import com.mustafa.melody.domain.model.Song

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    pagedSongs: LazyPagingItems<Song>? = null,
    likedSongIds: Set<String> = emptySet(),
    onIntent: (SearchIntent) -> Unit,
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
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onIntent(SearchIntent.SearchSubmitted) }
                ),
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
            } else if (uiState.isLoading || pagedSongs?.loadState?.refresh is LoadState.Loading) {
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
                            ListItem(
                                headlineContent = { Text(historyItem) },
                                leadingContent = {
                                    Icon(Icons.Default.History, contentDescription = null)
                                },
                                trailingContent = {
                                    IconButton(onClick = { onIntent(SearchIntent.RemoveHistoryItem(historyItem)) }) {
                                        Icon(Icons.Default.Close, stringResource(R.string.remove_search_history_item))
                                    }
                                },
                                modifier = Modifier.clickable {
                                    onIntent(SearchIntent.HistoryItemClicked(historyItem))
                                },
                            )
                        }
                    }
                }
            } else if ((pagedSongs?.itemCount ?: uiState.results.size) > 0) {
                LazyColumn {
                    if (pagedSongs != null) {
                        items(
                            count = pagedSongs.itemCount,
                            key = { index -> pagedSongs.peek(index)?.id ?: "placeholder-$index" },
                        ) { index ->
                            val song = pagedSongs[index]
                            if (song == null) SongCardShimmer()
                            else SongResult(song = song, isLiked = song.id in likedSongIds, onIntent = onIntent)
                        }
                        if (pagedSongs.loadState.append is LoadState.Loading) {
                            item { SongCardShimmer() }
                        }
                    } else items(uiState.results, key = { it.id }) { song ->
                        SongResult(song = song, isLiked = song.id in likedSongIds, onIntent = onIntent)
                    }
                }
            } else if (pagedSongs?.loadState?.refresh is LoadState.Error) {
                ErrorState(
                    title = stringResource(R.string.something_went_wrong),
                    description = stringResource(R.string.catalog_load_failed),
                    onRetry = pagedSongs::retry,
                )
            } else {
                EmptyState(
                    title = stringResource(R.string.no_results),
                    icon = Icons.Default.Search,
                )
            }
        }
    }
}

@Composable
private fun SongResult(song: Song, isLiked: Boolean, onIntent: (SearchIntent) -> Unit) {
                        SongCard(
                            title = song.title,
                            artistName = song.artistName,
                            coverImageUrl = song.coverImageUrl,
                            isLiked = isLiked,
                            onClick = { onIntent(SearchIntent.SongClicked(song.id)) },
                            onLikeClick = { onIntent(SearchIntent.SongLiked(song.id)) },
                        )
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
