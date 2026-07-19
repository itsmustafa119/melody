package com.mustafa.melody.di

import com.mustafa.melody.data.local.preferences.AppPreferencesRepositoryImpl
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindAppPreferencesRepository(
        implementation: AppPreferencesRepositoryImpl
    ): AppPreferencesRepository
}
