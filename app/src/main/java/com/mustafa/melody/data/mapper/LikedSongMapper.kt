package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.LikedSongEntity
import com.mustafa.melody.domain.model.LikedSong

fun LikedSongEntity.toDomain() = LikedSong(
    songId = songId,
    title = title,
    artistName = artistName,
    coverImageUrl = coverImageUrl,
    audioUrl = audioUrl,
    likedAt = likedAt
)

fun LikedSong.toEntity() = LikedSongEntity(
    songId = songId,
    title = title,
    artistName = artistName,
    coverImageUrl = coverImageUrl,
    audioUrl = audioUrl,
    likedAt = likedAt
)
