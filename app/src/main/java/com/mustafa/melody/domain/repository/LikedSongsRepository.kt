package com.mustafa.melody.domain.repository

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.LikedSong
import kotlinx.coroutines.flow.Flow

interface LikedSongsRepository {
    fun observeLikedSongs(): Flow<PagingData<LikedSong>>
    fun observeIsLiked(songId: String): Flow<Boolean>
    suspend fun saveLikedSong(song: LikedSong)
    suspend fun removeLikedSong(songId: String)
}
