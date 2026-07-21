package com.mustafa.melody.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recently_played",
    indices = [Index(value = ["played_at"])],
)
data class RecentlyPlayedEntity(
    @PrimaryKey @ColumnInfo(name = "song_id") val songId: String,
    val title: String,
    @ColumnInfo(name = "artist_name") val artistName: String,
    @ColumnInfo(name = "cover_image_url") val coverImageUrl: String,
    @ColumnInfo(name = "audio_url") val audioUrl: String,
    val album: String,
    @ColumnInfo(name = "played_at") val playedAt: Long,
)
