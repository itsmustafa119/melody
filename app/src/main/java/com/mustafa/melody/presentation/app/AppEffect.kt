package com.mustafa.melody.presentation.app

sealed interface AppEffect {
    data object PreferenceUpdateFailed : AppEffect
}
