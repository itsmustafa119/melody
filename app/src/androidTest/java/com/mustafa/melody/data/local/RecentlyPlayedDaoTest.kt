package com.mustafa.melody.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.melody.data.local.dao.RecentlyPlayedDao
import com.mustafa.melody.data.local.database.MelodyDatabase
import com.mustafa.melody.data.local.entity.RecentlyPlayedEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentlyPlayedDaoTest {
    private lateinit var database: MelodyDatabase
    private lateinit var dao: RecentlyPlayedDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodyDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.recentlyPlayedDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun latestPlaybackMovesSongToFrontWithoutCreatingDuplicates() = runTest {
        fun song(id: String, playedAt: Long) = RecentlyPlayedEntity(
            songId = id,
            title = "Song $id",
            artistName = "Artist",
            coverImageUrl = "cover",
            audioUrl = "audio",
            album = "Album",
            playedAt = playedAt,
        )

        dao.upsert(song("one", 10))
        dao.upsert(song("two", 20))
        dao.upsert(song("one", 30))

        val history = dao.observeRecent().first()
        assertEquals(listOf("one", "two"), history.map { it.songId })
        assertEquals(30, history.first().playedAt)
    }

    @Test
    fun clearRemovesHistory() = runTest {
        dao.upsert(RecentlyPlayedEntity("one", "Song", "Artist", "cover", "audio", "Album", 1))
        dao.clear()
        assertEquals(emptyList<RecentlyPlayedEntity>(), dao.observeRecent().first())
    }
}
