package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import com.mustafa.melody.domain.model.DownloadStatus
import com.mustafa.melody.domain.model.DownloadedSong

fun DownloadedSongEntity.toDomain() = DownloadedSong(
    songId = songId,
    title = title,
    artistName = artistName,
    coverImageUrl = coverImageUrl,
    remoteAudioUrl = remoteAudioUrl,
    localFilePath = localFilePath,
    downloadedAt = downloadedAt,
    fileSizeBytes = fileSizeBytes,
    status = DownloadStatus.entries.firstOrNull { it.name == status } ?: DownloadStatus.FAILED
)

fun DownloadedSong.toEntity() = DownloadedSongEntity(
    songId = songId,
    title = title,
    artistName = artistName,
    coverImageUrl = coverImageUrl,
    remoteAudioUrl = remoteAudioUrl,
    localFilePath = localFilePath,
    downloadedAt = downloadedAt,
    fileSizeBytes = fileSizeBytes,
    status = status.name
)
