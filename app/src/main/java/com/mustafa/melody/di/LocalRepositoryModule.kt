package com.mustafa.melody.di

import com.mustafa.melody.data.repository.ChatRepositoryImpl
import com.mustafa.melody.data.repository.DownloadsRepositoryImpl
import com.mustafa.melody.data.repository.LikedSongsRepositoryImpl
import com.mustafa.melody.data.repository.SearchHistoryRepositoryImpl
import com.mustafa.melody.domain.repository.ChatRepository
import com.mustafa.melody.domain.repository.DownloadsRepository
import com.mustafa.melody.domain.repository.LikedSongsRepository
import com.mustafa.melody.domain.repository.SearchHistoryRepository
import com.mustafa.melody.data.repository.MusicCatalogRepositoryImpl
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import com.mustafa.melody.data.repository.SocialRepositoryImpl
import com.mustafa.melody.domain.repository.SocialRepository
import com.mustafa.melody.data.repository.RealtimeChatRepositoryImpl
import com.mustafa.melody.domain.repository.RealtimeChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        implementation: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindLikedSongsRepository(
        implementation: LikedSongsRepositoryImpl
    ): LikedSongsRepository

    @Binds
    @Singleton
    abstract fun bindDownloadsRepository(
        implementation: DownloadsRepositoryImpl
    ): DownloadsRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        implementation: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMusicCatalogRepository(
        implementation: MusicCatalogRepositoryImpl
    ): MusicCatalogRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        implementation: SocialRepositoryImpl
    ): SocialRepository

    @Binds
    @Singleton
    abstract fun bindRealtimeChatRepository(
        implementation: RealtimeChatRepositoryImpl
    ): RealtimeChatRepository
}
