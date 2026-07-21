package com.mustafa.melody.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.melody.data.local.dao.SearchHistoryDao
import com.mustafa.melody.data.local.database.MelodyDatabase
import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchHistoryDaoTest {

    private lateinit var database: MelodyDatabase
    private lateinit var dao: SearchHistoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.searchHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadSearchHistory() = runTest {
        val entity = SearchHistoryEntity("query", 100L)
        dao.upsert(entity)
        val recent = dao.observeRecent(1).first()
        assertEquals(1, recent.size)
        assertEquals(entity, recent[0])
    }

    @Test
    fun ordersBySearchedAtDescending() = runTest {
        dao.upsert(SearchHistoryEntity("old", 100L))
        dao.upsert(SearchHistoryEntity("new", 200L))
        val recent = dao.observeRecent(2).first()
        assertEquals("new", recent[0].searchQuery)
        assertEquals("old", recent[1].searchQuery)
    }

    @Test
    fun updatesExistingQuery() = runTest {
        dao.upsert(SearchHistoryEntity("query", 100L))
        dao.upsert(SearchHistoryEntity("query", 200L))
        val recent = dao.observeRecent(1).first()
        assertEquals(1, recent.size)
        assertEquals(200L, recent[0].searchedAt)
    }

    @Test
    fun deleteByQuery() = runTest {
        dao.upsert(SearchHistoryEntity("q1", 100L))
        dao.deleteByQuery("q1")
        assertEquals(0, dao.count())
    }

    @Test
    fun clearAll() = runTest {
        dao.upsert(SearchHistoryEntity("q1", 100L))
        dao.upsert(SearchHistoryEntity("q2", 200L))
        dao.clearAll()
        assertEquals(0, dao.count())
    }
}
