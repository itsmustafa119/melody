package com.mustafa.melody.presentation.profile

data class ProfileUiState(
    val displayName: String = "",
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val avatarUrl: String? = null,
)
