package com.mustafa.melody.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.domain.model.Song
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogRepository: MusicCatalogRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(HomeUiState())
    val state = mutableState.asStateFlow()

    init { refresh() }

    fun onIntent(intent: HomeIntent) {
        if (intent == HomeIntent.Retry) refresh()
    }

    fun resolveSong(songId: String, onReady: (Song) -> Unit) {
        val loaded = with(mutableState.value) { recommendations + popularSongs + newestSongs }
            .firstOrNull { it.id == songId }
        if (loaded != null) onReady(loaded)
        else viewModelScope.launch { catalogRepository.song(songId)?.let(onReady) }
    }

    fun refresh() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(isLoading = true, errorMessageResId = null)
            runCatching { catalogRepository.homeCatalog() }
                .onSuccess { catalog ->
                    mutableState.value = HomeUiState(
                        isLoading = false,
                        recommendations = catalog.recommendations,
                        popularSongs = catalog.popular,
                        newestSongs = catalog.newest,
                        globalPlaylists = catalog.globalPlaylists,
                        localPlaylists = catalog.localPlaylists,
                        usingOfflineFallback = catalog.usingOfflineFallback,
                    )
                }
                .onFailure {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        errorMessageResId = com.mustafa.melody.R.string.catalog_load_failed,
                    )
                }
        }
    }
}
