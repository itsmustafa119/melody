package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.ChatMessageEntity
import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import com.mustafa.melody.data.local.entity.LikedSongEntity
import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import com.mustafa.melody.domain.model.DownloadStatus
import com.mustafa.melody.domain.model.MessageStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun `SearchHistoryEntity maps to domain correctly`() {
        val entity = SearchHistoryEntity("query", 123L)
        val domain = entity.toDomain()
        assertEquals("query", domain.query)
        assertEquals(123L, domain.searchedAt)
    }

    @Test
    fun `LikedSongEntity maps to domain correctly`() {
        val entity = LikedSongEntity("id", "title", "artist", "url", "audio", 123L)
        val domain = entity.toDomain()
        assertEquals("id", domain.songId)
        assertEquals("title", domain.title)
        assertEquals("artist", domain.artistName)
        assertEquals("url", domain.coverImageUrl)
        assertEquals("audio", domain.audioUrl)
        assertEquals(123L, domain.likedAt)
    }

    @Test
    fun `DownloadedSongEntity maps to domain correctly`() {
        val entity = DownloadedSongEntity(
            "id", "title", "artist", "url", "remote", "local", 123L, 456L, "COMPLETED"
        )
        val domain = entity.toDomain()
        assertEquals("id", domain.songId)
        assertEquals(DownloadStatus.COMPLETED, domain.status)
    }

    @Test
    fun `Invalid DownloadStatus maps to FAILED`() {
        val entity = DownloadedSongEntity(
            "id", "title", "artist", "url", "remote", "local", 123L, 456L, "INVALID"
        )
        assertEquals(DownloadStatus.FAILED, entity.toDomain().status)
    }

    @Test
    fun `ChatMessageEntity maps to domain correctly`() {
        val entity = ChatMessageEntity(
            "id", "conv", "sender", "receiver", "text", 123L, "SENT", "song"
        )
        val domain = entity.toDomain()
        assertEquals("id", domain.messageId)
        assertEquals(MessageStatus.SENT, domain.status)
    }

    @Test
    fun `Invalid MessageStatus maps to FAILED`() {
        val entity = ChatMessageEntity(
            "id", "conv", "sender", "receiver", "text", 123L, "INVALID", "song"
        )
        assertEquals(MessageStatus.FAILED, entity.toDomain().status)
    }
}
