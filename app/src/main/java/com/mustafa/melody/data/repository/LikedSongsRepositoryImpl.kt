package com.mustafa.melody.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafa.melody.data.local.dao.LikedSongDao
import com.mustafa.melody.data.local.database.DatabaseConstants
import com.mustafa.melody.data.mapper.toDomain
import com.mustafa.melody.data.mapper.toEntity
import com.mustafa.melody.domain.model.LikedSong
import com.mustafa.melody.domain.repository.LikedSongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedSongsRepositoryImpl @Inject constructor(
    private val likedSongDao: LikedSongDao
) : LikedSongsRepository {

    override fun observeLikedSongs(): Flow<PagingData<LikedSong>> {
        return Pager(
            config = PagingConfig(
                pageSize = DatabaseConstants.DEFAULT_PAGE_SIZE,
                prefetchDistance = DatabaseConstants.DEFAULT_PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { likedSongDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun observeIsLiked(songId: String): Flow<Boolean> {
        return likedSongDao.observeIsLiked(songId)
    }

    override suspend fun saveLikedSong(song: LikedSong) {
        likedSongDao.upsert(song.toEntity())
    }

    override suspend fun removeLikedSong(songId: String) {
        likedSongDao.deleteBySongId(songId)
    }
}
