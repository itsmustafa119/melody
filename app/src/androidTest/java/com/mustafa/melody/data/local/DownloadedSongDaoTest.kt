package com.mustafa.melody.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.database.MelodyDatabase
import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadedSongDaoTest {

    private lateinit var database: MelodyDatabase
    private lateinit var dao: DownloadedSongDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.downloadedSongDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndUpdateStatus() = runTest {
        val entity = DownloadedSongEntity("s1", "T", "A", null, "remote", null, null, null, "QUEUED")
        dao.upsert(entity)
        
        val updated = entity.copy(status = "COMPLETED", localFilePath = "path", downloadedAt = 100L)
        dao.upsert(updated)
        
        val observed = dao.observeBySongId("s1").first()
        assertNotNull(observed)
        assertEquals("COMPLETED", observed?.status)
        assertEquals("path", observed?.localFilePath)
    }

    @Test
    fun clearCompleted() = runTest {
        dao.upsert(DownloadedSongEntity("s1", "T", "A", null, null, null, null, null, "COMPLETED"))
        dao.upsert(DownloadedSongEntity("s2", "T", "A", null, null, null, null, null, "DOWNLOADING"))
        
        dao.clearCompleted("COMPLETED")
        assertEquals(1, dao.count())
    }
}
