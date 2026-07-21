package com.mustafa.melody.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Upsert
    suspend fun upsert(item: SearchHistoryEntity)

    @Query(
        """
        SELECT * FROM search_history
        ORDER BY searched_at DESC
        LIMIT :limit
        """
    )
    fun observeRecent(limit: Int): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun count(): Int
}
