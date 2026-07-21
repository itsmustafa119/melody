package com.mustafa.melody.domain.usecase.likedsongs

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.LikedSong
import com.mustafa.melody.domain.repository.LikedSongsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLikedSongsUseCase @Inject constructor(
    private val repository: LikedSongsRepository
) {
    operator fun invoke(): Flow<PagingData<LikedSong>> = repository.observeLikedSongs()
}
