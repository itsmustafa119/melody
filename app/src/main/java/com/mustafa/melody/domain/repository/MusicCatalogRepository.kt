package com.mustafa.melody.domain.repository

import androidx.paging.PagingData
import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.Song
import kotlinx.coroutines.flow.Flow

enum class CatalogFilter { ALL, SONGS, ARTISTS, ALBUMS }

data class HomeCatalog(
    val recommendations: List<Song>,
    val popular: List<Song>,
    val newest: List<Song>,
    val globalPlaylists: List<MusicPlaylist>,
    val localPlaylists: List<MusicPlaylist>,
    val usingOfflineFallback: Boolean,
)

interface MusicCatalogRepository {
    suspend fun homeCatalog(): HomeCatalog
    fun pagedSearch(query: String, filter: CatalogFilter): Flow<PagingData<Song>>
    fun pagedPlaylists(kind: com.mustafa.melody.domain.model.PlaylistKind): Flow<PagingData<MusicPlaylist>>
    fun pagedPlaylistSongs(playlistId: String): Flow<PagingData<Song>>
    suspend fun song(songId: String): Song?
    suspend fun playlists(): List<MusicPlaylist>
    suspend fun createPlaylist(title: String): MusicPlaylist
    suspend fun playlistSongs(playlistId: String): List<Song>
}
