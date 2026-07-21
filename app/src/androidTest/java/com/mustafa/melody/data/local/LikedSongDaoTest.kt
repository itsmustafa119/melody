package com.mustafa.melody.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.melody.data.local.dao.LikedSongDao
import com.mustafa.melody.data.local.database.MelodyDatabase
import com.mustafa.melody.data.local.entity.LikedSongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LikedSongDaoTest {

    private lateinit var database: MelodyDatabase
    private lateinit var dao: LikedSongDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.likedSongDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndObserveIsLiked() = runTest {
        val songId = "s1"
        dao.upsert(LikedSongEntity(songId, "T", "A", null, null, 100L))
        assertTrue(dao.observeIsLiked(songId).first())
    }

    @Test
    fun deleteBySongId() = runTest {
        val songId = "s1"
        dao.upsert(LikedSongEntity(songId, "T", "A", null, null, 100L))
        dao.deleteBySongId(songId)
        assertEquals(0, dao.count())
    }

    @Test
    fun pagingSourceReturnsStoredSongs() = runTest {
        dao.upsert(LikedSongEntity("s1", "T1", "A1", null, null, 100L))
        dao.upsert(LikedSongEntity("s2", "T2", "A2", null, null, 200L))
        
        val pagingSource = dao.pagingSource()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        ) as PagingSource.LoadResult.Page
        
        assertEquals(2, result.data.size)
        assertEquals("s2", result.data[0].songId) // Ordered by liked_at DESC
    }
}
