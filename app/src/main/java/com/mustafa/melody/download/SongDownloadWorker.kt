package com.mustafa.melody.download

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.local.entity.DownloadedSongEntity
import com.mustafa.melody.domain.model.DownloadStatus
import com.mustafa.melody.domain.model.Song
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DownloadWorkerEntryPoint {
    fun downloadedSongDao(): DownloadedSongDao
}

class SongDownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val artist = inputData.getString(KEY_ARTIST) ?: return Result.failure()
        val audioUrl = inputData.getString(KEY_AUDIO) ?: return Result.failure()
        val cover = inputData.getString(KEY_COVER)
        val dao = EntryPointAccessors.fromApplication(
            applicationContext,
            DownloadWorkerEntryPoint::class.java,
        ).downloadedSongDao()
        val targetDir = File(applicationContext.filesDir, "downloads").apply { mkdirs() }
        val target = File(targetDir, "$id.mp3")
        dao.upsert(entity(id, title, artist, cover, audioUrl, null, null, DownloadStatus.DOWNLOADING))
        return try {
            val connection = (URL(audioUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 30_000
            }
            connection.inputStream.use { input ->
                target.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var copied = 0L
                    val total = connection.contentLengthLong.coerceAtLeast(1L)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        copied += count
                        setProgress(workDataOf(KEY_PROGRESS to ((copied * 100 / total).coerceIn(0, 100)).toInt()))
                    }
                }
            }
            dao.upsert(entity(id, title, artist, cover, audioUrl, target.absolutePath, target.length(), DownloadStatus.COMPLETED))
            Result.success()
        } catch (error: Exception) {
            target.delete()
            dao.upsert(entity(id, title, artist, cover, audioUrl, null, null, DownloadStatus.FAILED))
            Result.retry()
        }
    }

    private fun entity(
        id: String, title: String, artist: String, cover: String?, remote: String,
        local: String?, size: Long?, status: DownloadStatus,
    ) = DownloadedSongEntity(
        songId = id,
        title = title,
        artistName = artist,
        coverImageUrl = cover,
        remoteAudioUrl = remote,
        localFilePath = local,
        downloadedAt = if (status == DownloadStatus.COMPLETED) System.currentTimeMillis() else null,
        fileSizeBytes = size,
        status = status.name,
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_ARTIST = "artist"
        const val KEY_AUDIO = "audio"
        const val KEY_COVER = "cover"
        const val KEY_PROGRESS = "progress"

        fun enqueue(context: Context, song: Song) {
            val data = Data.Builder()
                .putString(KEY_ID, song.id)
                .putString(KEY_TITLE, song.title)
                .putString(KEY_ARTIST, song.artistName)
                .putString(KEY_AUDIO, song.audioUrl)
                .putString(KEY_COVER, song.coverImageUrl)
                .build()
            val request = OneTimeWorkRequestBuilder<SongDownloadWorker>()
                .setInputData(data)
                .setConstraints(androidx.work.Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork("download-${song.id}", ExistingWorkPolicy.KEEP, request)
        }
    }
}
