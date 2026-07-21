package com.mustafa.melody.domain.model

data class Song(
    val id: String,
    val title: String,
    val artistName: String,
    val coverImageUrl: String,
    val audioUrl: String,
    val album: String,
    val isLocal: Boolean = false,
    val isLiked: Boolean = false,
)

data class MusicPlaylist(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverImageUrl: String,
    val songIds: List<String>,
    val kind: PlaylistKind,
)

enum class PlaylistKind { WORLD, LOCAL, USER }
