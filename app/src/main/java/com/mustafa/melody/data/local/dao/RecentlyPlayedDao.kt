package com.mustafa.melody.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.melody.data.local.entity.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {
    @Upsert
    suspend fun upsert(item: RecentlyPlayedEntity)

    @Query("SELECT * FROM recently_played ORDER BY played_at DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<RecentlyPlayedEntity>>

    @Query("DELETE FROM recently_played WHERE song_id = :songId")
    suspend fun delete(songId: String)

    @Query("DELETE FROM recently_played")
    suspend fun clear()
}
