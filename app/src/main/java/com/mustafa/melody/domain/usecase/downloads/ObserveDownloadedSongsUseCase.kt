package com.mustafa.melody.domain.usecase.downloads

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.DownloadedSong
import com.mustafa.melody.domain.repository.DownloadsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDownloadedSongsUseCase @Inject constructor(
    private val repository: DownloadsRepository
) {
    operator fun invoke(): Flow<PagingData<DownloadedSong>> = repository.observeDownloadedSongs()
}
