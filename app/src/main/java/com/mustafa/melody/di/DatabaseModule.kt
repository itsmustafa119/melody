package com.mustafa.melody.di

import android.content.Context
import androidx.room.Room
import com.mustafa.melody.data.local.dao.ChatMessageDao
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.dao.LikedSongDao
import com.mustafa.melody.data.local.dao.SearchHistoryDao
import com.mustafa.melody.data.local.database.DatabaseConstants
import com.mustafa.melody.data.local.database.MelodyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMelodyDatabase(
        @ApplicationContext context: Context
    ): MelodyDatabase {
        return Room.databaseBuilder(
            context,
            MelodyDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSearchHistoryDao(
        database: MelodyDatabase
    ): SearchHistoryDao = database.searchHistoryDao()

    @Provides
    fun provideLikedSongDao(
        database: MelodyDatabase
    ): LikedSongDao = database.likedSongDao()

    @Provides
    fun provideDownloadedSongDao(
        database: MelodyDatabase
    ): DownloadedSongDao = database.downloadedSongDao()

    @Provides
    fun provideChatMessageDao(
        database: MelodyDatabase
    ): ChatMessageDao = database.chatMessageDao()
}
