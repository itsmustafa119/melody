package com.mustafa.melody.domain.usecase.downloads

import com.mustafa.melody.domain.repository.DownloadsRepository
import javax.inject.Inject

class RemoveDownloadedSongUseCase @Inject constructor(
    private val repository: DownloadsRepository
) {
    suspend operator fun invoke(songId: String) = repository.removeDownloadedSong(songId)
}
