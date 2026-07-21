package com.mustafa.melody.domain.usecase.downloads

import com.mustafa.melody.domain.repository.DownloadsRepository
import javax.inject.Inject

class ClearCompletedDownloadsUseCase @Inject constructor(
    private val repository: DownloadsRepository
) {
    suspend operator fun invoke() = repository.clearCompletedDownloads()
}
