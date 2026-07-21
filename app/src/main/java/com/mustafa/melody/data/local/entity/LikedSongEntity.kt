package com.mustafa.melody.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "liked_songs",
    indices = [
        Index(value = ["liked_at"]),
        Index(value = ["artist_name"])
    ]
)
data class LikedSongEntity(
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    val songId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "cover_image_url")
    val coverImageUrl: String?,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String?,

    @ColumnInfo(name = "liked_at")
    val likedAt: Long
)
