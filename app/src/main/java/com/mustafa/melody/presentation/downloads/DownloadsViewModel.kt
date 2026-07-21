package com.mustafa.melody.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.mapper.toDomain
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafa.melody.domain.model.DownloadedSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map as mapPaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(private val dao: DownloadedSongDao) : ViewModel() {
    val songs = dao.observeAll()
        .map { rows -> rows.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun pagedSongs(sort: DownloadSortOption): Flow<PagingData<DownloadedSong>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false),
        pagingSourceFactory = {
            when (sort) {
                DownloadSortOption.RECENT -> dao.pagingSource()
                DownloadSortOption.TITLE -> dao.pagingSourceByTitle()
                DownloadSortOption.ARTIST -> dao.pagingSourceByArtist()
            }
        },
    ).flow.mapPaging { data -> data.map { it.toDomain() } }

    fun remove(songId: String, localPath: String?) {
        viewModelScope.launch {
            localPath?.let(::File)?.takeIf(File::exists)?.delete()
            dao.deleteBySongId(songId)
        }
    }
}
