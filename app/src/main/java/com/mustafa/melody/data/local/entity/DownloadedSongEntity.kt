package com.mustafa.melody.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "downloaded_songs",
    indices = [
        Index(value = ["downloaded_at"]),
        Index(value = ["status"]),
        Index(value = ["artist_name"])
    ]
)
data class DownloadedSongEntity(
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    val songId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "cover_image_url")
    val coverImageUrl: String?,

    @ColumnInfo(name = "remote_audio_url")
    val remoteAudioUrl: String?,

    @ColumnInfo(name = "local_file_path")
    val localFilePath: String?,

    @ColumnInfo(name = "downloaded_at")
    val downloadedAt: Long?,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long?,

    @ColumnInfo(name = "status")
    val status: String
)
