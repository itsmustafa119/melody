package com.mustafa.melody.data.remote.dto

import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.PlaylistKind
import com.mustafa.melody.domain.model.Song
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongDto(
    val id: String,
    val title: String,
    @SerialName("artist_name") val artistName: String,
    val album: String = "",
    @SerialName("cover_image_url") val coverImageUrl: String,
    @SerialName("audio_url") val audioUrl: String,
    @SerialName("is_local") val isLocal: Boolean = false,
    @SerialName("play_count") val playCount: Long = 0,
    @SerialName("created_at") val createdAt: String = "",
) {
    fun toDomain() = Song(id, title, artistName, coverImageUrl, audioUrl, album, isLocal)
}

@Serializable
data class PlaylistDto(
    val id: String,
    val title: String,
    val description: String = "",
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    val kind: String,
    @SerialName("is_public") val isPublic: Boolean = true,
) {
    fun toDomain() = MusicPlaylist(
        id = id,
        title = title,
        subtitle = description,
        coverImageUrl = coverImageUrl.orEmpty(),
        songIds = emptyList(),
        kind = runCatching { PlaylistKind.valueOf(kind) }.getOrDefault(PlaylistKind.USER),
    )
}

@Serializable
data class CreatePlaylistDto(
    @SerialName("owner_id") val ownerId: String?,
    val title: String,
    val description: String = "",
    val kind: String = "USER",
    @SerialName("is_public") val isPublic: Boolean = true,
)

@Serializable
data class PlaylistSongDto(@SerialName("song_id") val songId: String)
