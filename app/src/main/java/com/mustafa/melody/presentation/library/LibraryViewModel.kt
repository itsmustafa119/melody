package com.mustafa.melody.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.data.local.dao.LikedSongDao
import com.mustafa.melody.data.local.entity.LikedSongEntity
import com.mustafa.melody.data.local.dao.RecentlyPlayedDao
import com.mustafa.melody.domain.model.Song
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.paging.cachedIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val dao: LikedSongDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val catalogRepository: MusicCatalogRepository,
) : ViewModel() {
    val likedSongs = dao.observeAll().map { rows ->
        rows.map { row ->
            Song(row.songId, row.title, row.artistName, row.coverImageUrl.orEmpty(), row.audioUrl.orEmpty(), "Liked", isLiked = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentlyPlayed = recentlyPlayedDao.observeRecent().map { rows ->
        rows.map { row ->
            Song(row.songId, row.title, row.artistName, row.coverImageUrl, row.audioUrl, row.album)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pagedLikedSongs = Pager(PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false)) {
        dao.pagingSource()
    }.flow.map { data -> data.map { row ->
        Song(row.songId, row.title, row.artistName, row.coverImageUrl.orEmpty(), row.audioUrl.orEmpty(), "", isLiked = true)
    } }.cachedIn(viewModelScope)

    val pagedRecentlyPlayed = Pager(PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false)) {
        recentlyPlayedDao.pagingSource()
    }.flow.map { data -> data.map { row ->
        Song(row.songId, row.title, row.artistName, row.coverImageUrl, row.audioUrl, row.album)
    } }.cachedIn(viewModelScope)

    fun toggle(songId: String) {
        viewModelScope.launch {
            val existing = likedSongs.value.any { it.id == songId }
            if (existing) {
                dao.deleteBySongId(songId)
            } else {
                catalogRepository.song(songId)?.let { song ->
                    dao.upsert(LikedSongEntity(song.id, song.title, song.artistName, song.coverImageUrl, song.audioUrl, System.currentTimeMillis()))
                }
            }
        }
    }

    fun removeRecent(songId: String) {
        viewModelScope.launch { recentlyPlayedDao.delete(songId) }
    }
}
