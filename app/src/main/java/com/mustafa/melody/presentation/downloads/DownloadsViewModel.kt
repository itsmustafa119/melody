package com.mustafa.melody.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafa.melody.data.local.dao.DownloadedSongDao
import com.mustafa.melody.data.mapper.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(private val dao: DownloadedSongDao) : ViewModel() {
    val songs = dao.observeAll()
        .map { rows -> rows.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remove(songId: String, localPath: String?) {
        viewModelScope.launch {
            localPath?.let(::File)?.takeIf(File::exists)?.delete()
            dao.deleteBySongId(songId)
        }
    }
}
