package com.mustafa.melody.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.dao.LikedSongDao
import com.mustafa.melody.data.local.dao.SearchHistoryDao
import com.mustafa.melody.data.local.entity.ChatMessageEntity
import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import com.mustafa.melody.data.local.entity.LikedSongEntity
import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import com.mustafa.melody.data.local.dao.RecentlyPlayedDao
import com.mustafa.melody.data.local.entity.RecentlyPlayedEntity
import androidx.room.AutoMigration

@Database(
    entities = [
        SearchHistoryEntity::class,
        LikedSongEntity::class,
        DownloadedSongEntity::class,
        ChatMessageEntity::class,
        RecentlyPlayedEntity::class,
    ],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    exportSchema = true
)
abstract class MelodyDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun likedSongDao(): LikedSongDao

    abstract fun downloadedSongDao(): DownloadedSongDao

    abstract fun chatMessageDao(): ChatMessageDao

    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
}
