package com.mustafa.melody.domain.repository

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.DownloadedSong
import kotlinx.coroutines.flow.Flow

interface DownloadsRepository {
    fun observeDownloadedSongs(): Flow<PagingData<DownloadedSong>>
    fun observeDownloadedSong(songId: String): Flow<DownloadedSong?>
    suspend fun saveDownloadedSong(song: DownloadedSong)
    suspend fun removeDownloadedSong(songId: String)
    suspend fun clearCompletedDownloads()
}
