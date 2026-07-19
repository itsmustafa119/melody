package com.mustafa.melody.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.domain.usecase.preferences.ObserveAppPreferencesUseCase
import com.mustafa.melody.domain.usecase.preferences.SetFontScaleUseCase
import com.mustafa.melody.domain.usecase.preferences.SetLanguageUseCase
import com.mustafa.melody.domain.usecase.preferences.SetPremiumStatusUseCase
import com.mustafa.melody.domain.usecase.preferences.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setFontScaleUseCase: SetFontScaleUseCase,
    private val setPremiumStatusUseCase: SetPremiumStatusUseCase,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = observeAppPreferencesUseCase()
        .map { preferences ->
            AppUiState(
                isLoading = false,
                themeMode = preferences.themeMode,
                language = preferences.language,
                fontScale = preferences.fontScale,
                isPremium = preferences.isPremium
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppUiState(isLoading = true)
        )

    private val _effect = MutableSharedFlow<AppEffect>()
    val effect: SharedFlow<AppEffect> = _effect.asSharedFlow()

    fun onIntent(intent: AppIntent) {
        viewModelScope.launch {
            try {
                when (intent) {
                    is AppIntent.ChangeTheme -> setThemeModeUseCase(intent.themeMode)
                    is AppIntent.ChangeLanguage -> setLanguageUseCase(intent.language)
                    is AppIntent.ChangeFontScale -> setFontScaleUseCase(intent.fontScale)
                    is AppIntent.ChangePremiumStatus -> setPremiumStatusUseCase(intent.isPremium)
                }
            } catch (_: Exception) {
                _effect.emit(AppEffect.PreferenceUpdateFailed)
            }
        }
    }
}
