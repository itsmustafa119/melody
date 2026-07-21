package com.mustafa.melody.domain.usecase.likedsongs

import com.mustafa.melody.domain.model.LikedSong
import com.mustafa.melody.domain.repository.LikedSongsRepository
import javax.inject.Inject

class SaveLikedSongUseCase @Inject constructor(
    private val repository: LikedSongsRepository
) {
    suspend operator fun invoke(song: LikedSong) = repository.saveLikedSong(song)
}
