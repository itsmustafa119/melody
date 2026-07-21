package com.mustafa.melody.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.database.DatabaseConstants
import com.mustafa.melody.data.mapper.toDomain
import com.mustafa.melody.data.mapper.toEntity
import com.mustafa.melody.domain.model.DownloadStatus
import com.mustafa.melody.domain.model.DownloadedSong
import com.mustafa.melody.domain.repository.DownloadsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadsRepositoryImpl @Inject constructor(
    private val downloadedSongDao: DownloadedSongDao
) : DownloadsRepository {

    override fun observeDownloadedSongs(): Flow<PagingData<DownloadedSong>> {
        return Pager(
            config = PagingConfig(
                pageSize = DatabaseConstants.DEFAULT_PAGE_SIZE,
                prefetchDistance = DatabaseConstants.DEFAULT_PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { downloadedSongDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun observeDownloadedSong(songId: String): Flow<DownloadedSong?> {
        return downloadedSongDao.observeBySongId(songId).map { it?.toDomain() }
    }

    override suspend fun saveDownloadedSong(song: DownloadedSong) {
        downloadedSongDao.upsert(song.toEntity())
    }

    override suspend fun removeDownloadedSong(songId: String) {
        downloadedSongDao.deleteBySongId(songId)
    }

    override suspend fun clearCompletedDownloads() {
        downloadedSongDao.clearCompleted(DownloadStatus.COMPLETED.name)
    }
}
