package com.mustafa.melody.domain.usecase.downloads

import com.mustafa.melody.domain.model.DownloadedSong
import com.mustafa.melody.domain.repository.DownloadsRepository
import javax.inject.Inject

class SaveDownloadedSongUseCase @Inject constructor(
    private val repository: DownloadsRepository
) {
    suspend operator fun invoke(song: DownloadedSong) = repository.saveDownloadedSong(song)
}
