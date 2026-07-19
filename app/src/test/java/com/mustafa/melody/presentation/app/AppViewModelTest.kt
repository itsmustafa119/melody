package com.mustafa.melody.presentation.app

import com.mustafa.melody.domain.model.AppFontScale
import com.mustafa.melody.domain.model.AppLanguage
import com.mustafa.melody.domain.model.AppPreferences
import com.mustafa.melody.domain.model.ThemeMode
import com.mustafa.melody.domain.repository.AppPreferencesRepository
import com.mustafa.melody.domain.usecase.preferences.ObserveAppPreferencesUseCase
import com.mustafa.melody.domain.usecase.preferences.SetFontScaleUseCase
import com.mustafa.melody.domain.usecase.preferences.SetLanguageUseCase
import com.mustafa.melody.domain.usecase.preferences.SetPremiumStatusUseCase
import com.mustafa.melody.domain.usecase.preferences.SetThemeModeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAppPreferencesRepository
    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAppPreferencesRepository()
        
        viewModel = AppViewModel(
            ObserveAppPreferencesUseCase(fakeRepository),
            SetThemeModeUseCase(fakeRepository),
            SetLanguageUseCase(fakeRepository),
            SetFontScaleUseCase(fakeRepository),
            SetPremiumStatusUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `when preferences are emitted, loading should be false and values should match`() = runTest {
        // Start collecting to trigger WhileSubscribed
        val job = launch(testDispatcher) { viewModel.uiState.collect() }
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse("State should not be loading after preferences emitted", state.isLoading)
        assertEquals(ThemeMode.SYSTEM, state.themeMode)
        assertEquals(AppLanguage.SYSTEM, state.language)
        assertEquals(AppFontScale.DEFAULT, state.fontScale)
        assertFalse(state.isPremium)
        
        job.cancel()
    }

    @Test
    fun `ChangeTheme intent should update repository`() = runTest {
        val job = launch(testDispatcher) { viewModel.uiState.collect() }
        
        viewModel.onIntent(AppIntent.ChangeTheme(ThemeMode.DARK))
        advanceUntilIdle()
        
        val updatedPreferences = fakeRepository.preferences.first()
        assertEquals(ThemeMode.DARK, updatedPreferences.themeMode)
        
        job.cancel()
    }

    @Test
    fun `ChangeLanguage intent should update repository`() = runTest {
        val job = launch(testDispatcher) { viewModel.uiState.collect() }
        
        viewModel.onIntent(AppIntent.ChangeLanguage(AppLanguage.PERSIAN))
        advanceUntilIdle()
        
        val updatedPreferences = fakeRepository.preferences.first()
        assertEquals(AppLanguage.PERSIAN, updatedPreferences.language)
        
        job.cancel()
    }

    private class FakeAppPreferencesRepository : AppPreferencesRepository {
        private val _preferences = MutableStateFlow(AppPreferences())
        override val preferences: Flow<AppPreferences> = _preferences

        override suspend fun setThemeMode(themeMode: ThemeMode) {
            _preferences.value = _preferences.value.copy(themeMode = themeMode)
        }

        override suspend fun setLanguage(language: AppLanguage) {
            _preferences.value = _preferences.value.copy(language = language)
        }

        override suspend fun setFontScale(fontScale: AppFontScale) {
            _preferences.value = _preferences.value.copy(fontScale = fontScale)
        }

        override suspend fun setPremiumStatus(isPremium: Boolean) {
            _preferences.value = _preferences.value.copy(isPremium = isPremium)
        }
    }
}
