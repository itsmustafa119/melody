package com.mustafa.melody.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.R
import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.PlaylistKind
import com.mustafa.melody.domain.model.Song
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailsUiState(
    val playlist: MusicPlaylist? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
)

sealed interface PlaylistsEffect {
    data object PlaylistCreated : PlaylistsEffect
    data class ShowError(val messageResId: Int) : PlaylistsEffect
}

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val catalogRepository: MusicCatalogRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(PlaylistsUiState())
    val state = mutableState.asStateFlow()
    private val mutableDetails = MutableStateFlow(PlaylistDetailsUiState())
    val details = mutableDetails.asStateFlow()
    private val effectChannel = Channel<PlaylistsEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    private var allPlaylists: List<MusicPlaylist> = emptyList()

    init { refresh() }

    fun onIntent(intent: PlaylistsIntent) {
        when (intent) {
            is PlaylistsIntent.SectionSelected -> selectSection(intent.section)
            is PlaylistsIntent.CreatePlaylist -> create(intent.title)
            PlaylistsIntent.Retry -> refresh()
            else -> Unit
        }
    }

    fun openPlaylist(playlistId: String) {
        viewModelScope.launch {
            val playlist = allPlaylists.find { it.id == playlistId }
            mutableDetails.value = PlaylistDetailsUiState(playlist = playlist, isLoading = true)
            runCatching { catalogRepository.playlistSongs(playlistId) }
                .onSuccess { songs -> mutableDetails.value = PlaylistDetailsUiState(playlist, songs) }
                .onFailure { mutableDetails.value = PlaylistDetailsUiState(playlist, errorMessageResId = R.string.catalog_load_failed) }
        }
    }

    fun song(songId: String): Song? = mutableDetails.value.songs.find { it.id == songId }

    private fun refresh() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(isLoading = true, errorMessageResId = null)
            runCatching { catalogRepository.playlists() }
                .onSuccess {
                    allPlaylists = it
                    publishSection(mutableState.value.selectedSection)
                }
                .onFailure {
                    mutableState.value = mutableState.value.copy(isLoading = false, errorMessageResId = R.string.catalog_load_failed)
                }
        }
    }

    private fun selectSection(section: PlaylistSection) {
        publishSection(section)
    }

    private fun publishSection(section: PlaylistSection) {
        val kind = when (section) {
            PlaylistSection.WORLD -> PlaylistKind.WORLD
            PlaylistSection.LOCAL -> PlaylistKind.LOCAL
            PlaylistSection.USER -> PlaylistKind.USER
        }
        mutableState.value = PlaylistsUiState(
            isLoading = false,
            selectedSection = section,
            playlists = allPlaylists.filter { it.kind == kind },
        )
    }

    private fun create(title: String) {
        viewModelScope.launch {
            runCatching { catalogRepository.createPlaylist(title) }
                .onSuccess { created ->
                    allPlaylists = allPlaylists + created
                    publishSection(PlaylistSection.USER)
                    effectChannel.send(PlaylistsEffect.PlaylistCreated)
                }
                .onFailure { effectChannel.send(PlaylistsEffect.ShowError(R.string.sign_in_to_create_playlist)) }
        }
    }
}
