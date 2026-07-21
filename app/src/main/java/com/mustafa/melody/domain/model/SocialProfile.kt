package com.mustafa.melody.domain.model

data class SocialProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String,
    val isPremium: Boolean,
)
