package com.mustafa.melody.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.melody.data.local.entity.LikedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedSongDao {

    @Upsert
    suspend fun upsert(song: LikedSongEntity)

    @Query("DELETE FROM liked_songs WHERE song_id = :songId")
    suspend fun deleteBySongId(songId: String)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM liked_songs
            WHERE song_id = :songId
        )
        """
    )
    fun observeIsLiked(songId: String): Flow<Boolean>

    @Query(
        """
        SELECT * FROM liked_songs
        ORDER BY liked_at DESC
        """
    )
    fun observeAll(): Flow<List<LikedSongEntity>>

    @Query(
        """
        SELECT * FROM liked_songs
        ORDER BY liked_at DESC
        """
    )
    fun pagingSource(): PagingSource<Int, LikedSongEntity>

    @Query("SELECT COUNT(*) FROM liked_songs")
    suspend fun count(): Int
}
