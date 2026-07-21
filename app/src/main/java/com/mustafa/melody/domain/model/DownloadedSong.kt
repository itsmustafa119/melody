package com.mustafa.melody.domain.model

data class DownloadedSong(
    val songId: String,
    val title: String,
    val artistName: String,
    val coverImageUrl: String?,
    val remoteAudioUrl: String?,
    val localFilePath: String?,
    val downloadedAt: Long?,
    val fileSizeBytes: Long?,
    val status: DownloadStatus
)
