package com.mustafa.melody.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.local.database.MelodyDatabase
import com.mustafa.melody.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatMessageDaoTest {

    private lateinit var database: MelodyDatabase
    private lateinit var dao: ChatMessageDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.chatMessageDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndObserveConversation() = runTest {
        val m1 = ChatMessageEntity("1", "c1", "s", "r", "hi", 100L, "SENT", null)
        val m2 = ChatMessageEntity("2", "c1", "s", "r", "bye", 200L, "SENT", null)
        dao.upsertAll(listOf(m1, m2))
        
        val messages = dao.observeConversation("c1").first()
        assertEquals(2, messages.size)
        assertEquals("1", messages[0].messageId) // Ordered by sent_at ASC
    }

    @Test
    fun updateStatus() = runTest {
        dao.upsert(ChatMessageEntity("1", "c1", "s", "r", "hi", 100L, "SENT", null))
        dao.updateStatus("1", "READ")
        
        val messages = dao.observeConversation("c1").first()
        assertEquals("READ", messages[0].status)
    }

    @Test
    fun deleteConversation() = runTest {
        dao.upsert(ChatMessageEntity("1", "c1", "s", "r", "hi", 100L, "SENT", null))
        dao.upsert(ChatMessageEntity("2", "c2", "s", "r", "yo", 200L, "SENT", null))
        
        dao.deleteConversation("c1")
        assertEquals(1, dao.countConversationMessages("c2"))
        assertEquals(0, dao.countConversationMessages("c1"))
    }
}
