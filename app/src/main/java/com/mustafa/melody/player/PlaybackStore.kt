package com.mustafa.melody.player

import android.content.Context
import android.content.Intent
import com.mustafa.melody.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaybackUiState(
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val queueSize: Int = 0,
    val currentIndex: Int = 0,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false,
)

object PlaybackStore {
    private val mutableState = MutableStateFlow(PlaybackUiState())
    val state = mutableState.asStateFlow()

    internal fun update(transform: (PlaybackUiState) -> PlaybackUiState) {
        mutableState.value = transform(mutableState.value)
    }

    fun play(context: Context, song: Song) {
        playQueue(context, listOf(song))
    }

    fun playQueue(context: Context, songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        val safeIndex = startIndex.coerceIn(songs.indices)
        update {
            it.copy(
                song = songs[safeIndex],
                isPlaying = true,
                queueSize = songs.size,
                currentIndex = safeIndex,
                hasPrevious = safeIndex > 0,
                hasNext = safeIndex < songs.lastIndex,
            )
        }
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_PLAY
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_IDS, ArrayList(songs.map(Song::id)))
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_TITLES, ArrayList(songs.map(Song::title)))
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_ARTISTS, ArrayList(songs.map(Song::artistName)))
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_COVERS, ArrayList(songs.map(Song::coverImageUrl)))
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_AUDIOS, ArrayList(songs.map(Song::audioUrl)))
            putStringArrayListExtra(MelodyPlaybackService.EXTRA_ALBUMS, ArrayList(songs.map(Song::album)))
            putExtra(MelodyPlaybackService.EXTRA_START_INDEX, safeIndex)
        })
    }

    fun toggle(context: Context) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_TOGGLE
        })
    }

    fun seekTo(context: Context, positionMs: Long) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_SEEK
            putExtra(MelodyPlaybackService.EXTRA_POSITION, positionMs)
        })
    }

    fun next(context: Context) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_NEXT
        })
    }

    fun previous(context: Context) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_PREVIOUS
        })
    }

    fun setSpeed(context: Context, speed: Float) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_SPEED
            putExtra(MelodyPlaybackService.EXTRA_SPEED, speed)
        })
    }

    fun startSleepTimer(context: Context, minutes: Int = 30) {
        context.startService(Intent(context, MelodyPlaybackService::class.java).apply {
            action = MelodyPlaybackService.ACTION_SLEEP
            putExtra(MelodyPlaybackService.EXTRA_MINUTES, minutes)
        })
    }
}
