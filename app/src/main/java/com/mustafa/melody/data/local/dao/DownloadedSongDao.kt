package com.mustafa.melody.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedSongDao {

    @Upsert
    suspend fun upsert(song: DownloadedSongEntity)

    @Query(
        """
        SELECT * FROM downloaded_songs
        WHERE song_id = :songId
        LIMIT 1
        """
    )
    fun observeBySongId(songId: String): Flow<DownloadedSongEntity?>

    @Query("SELECT * FROM downloaded_songs WHERE song_id = :songId LIMIT 1")
    suspend fun getBySongId(songId: String): DownloadedSongEntity?

    @Query(
        """
        SELECT * FROM downloaded_songs
        ORDER BY downloaded_at DESC
        """
    )
    fun observeAll(): Flow<List<DownloadedSongEntity>>

    @Query(
        """
        SELECT * FROM downloaded_songs
        ORDER BY downloaded_at DESC
        """
    )
    fun pagingSource(): PagingSource<Int, DownloadedSongEntity>

    @Query("SELECT * FROM downloaded_songs ORDER BY title COLLATE NOCASE ASC")
    fun pagingSourceByTitle(): PagingSource<Int, DownloadedSongEntity>

    @Query("SELECT * FROM downloaded_songs ORDER BY artist_name COLLATE NOCASE ASC")
    fun pagingSourceByArtist(): PagingSource<Int, DownloadedSongEntity>

    @Query("DELETE FROM downloaded_songs WHERE song_id = :songId")
    suspend fun deleteBySongId(songId: String)

    @Query(
        """
        DELETE FROM downloaded_songs
        WHERE status = :completedStatus
        """
    )
    suspend fun clearCompleted(completedStatus: String)

    @Query("SELECT COUNT(*) FROM downloaded_songs")
    suspend fun count(): Int
}
