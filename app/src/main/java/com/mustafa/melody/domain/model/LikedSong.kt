package com.mustafa.melody.domain.model

data class LikedSong(
    val songId: String,
    val title: String,
    val artistName: String,
    val coverImageUrl: String?,
    val audioUrl: String?,
    val likedAt: Long
)
