package com.mustafa.melody.domain.usecase.likedsongs

import com.mustafa.melody.domain.repository.LikedSongsRepository
import javax.inject.Inject

class RemoveLikedSongUseCase @Inject constructor(
    private val repository: LikedSongsRepository
) {
    suspend operator fun invoke(songId: String) = repository.removeLikedSong(songId)
}
