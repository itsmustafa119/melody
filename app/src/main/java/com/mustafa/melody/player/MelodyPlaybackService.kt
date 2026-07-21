package com.mustafa.melody.player

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.core.net.toUri
import android.os.Handler
import android.os.Looper
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.dao.RecentlyPlayedDao
import com.mustafa.melody.data.local.entity.RecentlyPlayedEntity
import com.mustafa.melody.domain.model.DownloadStatus
import com.mustafa.melody.domain.model.Song
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MelodyPlaybackService : MediaSessionService() {
    @Inject lateinit var downloadedSongDao: DownloadedSongDao
    @Inject lateinit var recentlyPlayedDao: RecentlyPlayedDao

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var cache: SimpleCache
    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val sleepAction = Runnable { player.pause() }
    private var queueSongs: List<Song> = emptyList()
    private val progressAction = object : Runnable {
        override fun run() {
            if (::player.isInitialized) {
                val duration = player.duration.takeIf { it > 0 && it != C.TIME_UNSET } ?: 0
                val position = player.currentPosition.coerceAtLeast(0)
                // A short fade at track boundaries avoids an abrupt transition while retaining
                // gapless queue playback supported by ExoPlayer.
                val remaining = (duration - position).coerceAtLeast(0)
                player.volume = if (duration > 0 && remaining < CROSSFADE_MS) {
                    (remaining.toFloat() / CROSSFADE_MS).coerceIn(MIN_FADE_VOLUME, 1f)
                } else 1f
                updatePlaybackState(position, duration)
            }
            handler.postDelayed(this, PROGRESS_INTERVAL_MS)
        }
    }

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            PlaybackStore.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            updatePlaybackState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            player.volume = 1f
            val index = player.currentMediaItemIndex.coerceAtLeast(0)
            val song = queueSongs.getOrNull(index) ?: return
            PlaybackStore.update {
                it.copy(
                    song = song,
                    positionMs = 0,
                    currentIndex = index,
                    queueSize = queueSongs.size,
                    hasPrevious = player.hasPreviousMediaItem(),
                    hasNext = player.hasNextMediaItem(),
                )
            }
            serviceScope.launch {
                recentlyPlayedDao.upsert(
                    RecentlyPlayedEntity(
                        songId = song.id,
                        title = song.title,
                        artistName = song.artistName,
                        coverImageUrl = song.coverImageUrl,
                        audioUrl = song.audioUrl,
                        album = song.album,
                        playedAt = System.currentTimeMillis(),
                    ),
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        cache = SimpleCache(
            cacheDir.resolve("media"),
            LeastRecentlyUsedCacheEvictor(512L * 1024 * 1024),
            StandaloneDatabaseProvider(this),
        )
        val upstream = DefaultDataSource.Factory(this)
        val cacheFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(cacheFactory))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .also { it.addListener(listener) }
        mediaSession = MediaSession.Builder(this, player).build()
        handler.post(progressAction)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> serviceScope.launch { play(intent) }
            ACTION_TOGGLE -> if (player.isPlaying) player.pause() else player.play()
            ACTION_SEEK -> player.seekTo(intent.getLongExtra(EXTRA_POSITION, 0))
            ACTION_NEXT -> if (player.hasNextMediaItem()) player.seekToNextMediaItem()
            ACTION_PREVIOUS -> if (player.currentPosition > RESTART_THRESHOLD_MS) player.seekTo(0)
                else if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem()
            ACTION_SPEED -> player.playbackParameters = PlaybackParameters(intent.getFloatExtra(EXTRA_SPEED, 1f))
            ACTION_SLEEP -> {
                handler.removeCallbacks(sleepAction)
                handler.postDelayed(sleepAction, intent.getIntExtra(EXTRA_MINUTES, 30) * 60_000L)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun play(intent: Intent) {
        val ids = intent.getStringArrayListExtra(EXTRA_IDS).orEmpty()
        val titles = intent.getStringArrayListExtra(EXTRA_TITLES).orEmpty()
        val artists = intent.getStringArrayListExtra(EXTRA_ARTISTS).orEmpty()
        val covers = intent.getStringArrayListExtra(EXTRA_COVERS).orEmpty()
        val audios = intent.getStringArrayListExtra(EXTRA_AUDIOS).orEmpty()
        val albums = intent.getStringArrayListExtra(EXTRA_ALBUMS).orEmpty()
        if (ids.isEmpty() || ids.size != audios.size) return
        queueSongs = ids.indices.map { index ->
            Song(
                id = ids[index],
                title = titles.getOrElse(index) { "" },
                artistName = artists.getOrElse(index) { "" },
                coverImageUrl = covers.getOrElse(index) { "" },
                audioUrl = audios[index],
                album = albums.getOrElse(index) { "" },
            )
        }
        val items = queueSongs.map { song -> createMediaItem(song) }
        val startIndex = intent.getIntExtra(EXTRA_START_INDEX, 0).coerceIn(items.indices)
        player.setMediaItems(items, startIndex, 0)
        player.prepare()
        player.play()
    }

    private suspend fun createMediaItem(song: Song): MediaItem {
        val download = downloadedSongDao.getBySongId(song.id)
        val localFile = download?.localFilePath?.let(::File)
        val playbackUri = if (
            download?.status == DownloadStatus.COMPLETED.name && localFile?.isFile == true
        ) Uri.fromFile(localFile) else song.audioUrl.toUri()
        return MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(playbackUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artistName)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.coverImageUrl.takeIf(String::isNotBlank)?.let(Uri::parse))
                    .build(),
            )
            .build()
    }

    private fun updatePlaybackState(
        position: Long = player.currentPosition.coerceAtLeast(0),
        duration: Long = player.duration.takeIf { value -> value > 0 && value != C.TIME_UNSET } ?: 0,
    ) {
        PlaybackStore.update {
            it.copy(
                isPlaying = player.isPlaying,
                positionMs = position,
                durationMs = duration,
                currentIndex = player.currentMediaItemIndex.coerceAtLeast(0),
                queueSize = player.mediaItemCount,
                hasPrevious = player.hasPreviousMediaItem(),
                hasNext = player.hasNextMediaItem(),
            )
        }
    }

    override fun onDestroy() {
        player.removeListener(listener)
        mediaSession.release()
        player.release()
        cache.release()
        handler.removeCallbacks(sleepAction)
        handler.removeCallbacks(progressAction)
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY = "com.mustafa.melody.PLAY"
        const val ACTION_TOGGLE = "com.mustafa.melody.TOGGLE"
        const val ACTION_SEEK = "com.mustafa.melody.SEEK"
        const val ACTION_NEXT = "com.mustafa.melody.NEXT"
        const val ACTION_PREVIOUS = "com.mustafa.melody.PREVIOUS"
        const val ACTION_SPEED = "com.mustafa.melody.SPEED"
        const val ACTION_SLEEP = "com.mustafa.melody.SLEEP"
        const val EXTRA_IDS = "song_ids"
        const val EXTRA_TITLES = "song_titles"
        const val EXTRA_ARTISTS = "song_artists"
        const val EXTRA_COVERS = "song_covers"
        const val EXTRA_AUDIOS = "song_audios"
        const val EXTRA_ALBUMS = "song_albums"
        const val EXTRA_START_INDEX = "start_index"
        const val EXTRA_POSITION = "position_ms"
        const val EXTRA_SPEED = "speed"
        const val EXTRA_MINUTES = "minutes"
        private const val PROGRESS_INTERVAL_MS = 500L
        private const val CROSSFADE_MS = 3_000L
        private const val RESTART_THRESHOLD_MS = 5_000L
        private const val MIN_FADE_VOLUME = 0.08f
    }
}
