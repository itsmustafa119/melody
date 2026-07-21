package com.mustafa.melody.domain.usecase.likedsongs

import com.mustafa.melody.domain.repository.LikedSongsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsSongLikedUseCase @Inject constructor(
    private val repository: LikedSongsRepository
) {
    operator fun invoke(songId: String): Flow<Boolean> = repository.observeIsLiked(songId)
}
