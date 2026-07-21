package com.mustafa.melody.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mustafa.melody.data.catalog.DemoCatalog
import com.mustafa.melody.data.remote.SupabaseProvider
import com.mustafa.melody.data.remote.dto.PlaylistDto
import com.mustafa.melody.data.remote.dto.SongDto
import com.mustafa.melody.data.remote.dto.CreatePlaylistDto
import com.mustafa.melody.data.remote.dto.PlaylistSongDto
import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.PlaylistKind
import com.mustafa.melody.domain.model.Song
import com.mustafa.melody.domain.repository.CatalogFilter
import com.mustafa.melody.domain.repository.HomeCatalog
import com.mustafa.melody.domain.repository.MusicCatalogRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.auth.auth
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicCatalogRepositoryImpl @Inject constructor(
    private val provider: SupabaseProvider,
) : MusicCatalogRepository {
    private val localUserPlaylists = mutableListOf<MusicPlaylist>()
    override suspend fun homeCatalog(): HomeCatalog {
        val remoteSongs = fetchSongs(0, 50)
        val remotePlaylists = fetchRemotePlaylists()
        val songs = remoteSongs.getOrElse { DemoCatalog.songs }
        val playlists = remotePlaylists.getOrElse { DemoCatalog.playlists }
        return HomeCatalog(
            recommendations = songs.take(8),
            popular = songs.sortedByDescending { song ->
                remoteSongMetadata[song.id]?.playCount ?: 0
            }.take(8),
            newest = songs.takeLast(8).reversed(),
            globalPlaylists = playlists.filter { it.kind == PlaylistKind.WORLD },
            localPlaylists = playlists.filter { it.kind == PlaylistKind.LOCAL },
            usingOfflineFallback = remoteSongs.isFailure || remotePlaylists.isFailure,
        )
    }

    override fun pagedSearch(query: String, filter: CatalogFilter) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 8, enablePlaceholders = false),
        pagingSourceFactory = { CatalogPagingSource(query, filter) },
    ).flow

    override fun pagedPlaylists(kind: PlaylistKind) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 8, enablePlaceholders = false),
        pagingSourceFactory = { PlaylistPagingSource(kind) },
    ).flow

    override fun pagedPlaylistSongs(playlistId: String) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 8, enablePlaceholders = false),
        pagingSourceFactory = { PlaylistSongsPagingSource(playlistId) },
    ).flow

    override suspend fun song(songId: String): Song? =
        runCatching {
            provider.client?.from("songs")?.select {
                filter { eq("id", songId) }
                limit(1)
            }?.decodeSingleOrNull<SongDto>()?.toDomain()
        }.getOrNull() ?: DemoCatalog.songs.find { it.id == songId }

    override suspend fun playlists(): List<MusicPlaylist> =
        fetchRemotePlaylists().getOrElse { DemoCatalog.playlists + localUserPlaylists }

    override suspend fun createPlaylist(title: String): MusicPlaylist {
        val client = provider.client
        if (client != null) {
            val created = client.from("playlists").insert(
                CreatePlaylistDto(
                    ownerId = client.auth.currentUserOrNull()?.id,
                    title = title,
                    description = "0 songs",
                ),
            ) { select() }.decodeSingle<PlaylistDto>().toDomain()
            return created
        }
        return MusicPlaylist(
            id = UUID.randomUUID().toString(),
            title = title,
            subtitle = "0 songs",
            coverImageUrl = "https://picsum.photos/seed/${title.hashCode()}/600/600",
            songIds = emptyList(),
            kind = PlaylistKind.USER,
        ).also(localUserPlaylists::add)
    }

    override suspend fun playlistSongs(playlistId: String): List<Song> {
        val local = (DemoCatalog.playlists + localUserPlaylists).find { it.id == playlistId }
        if (local != null) return local.songIds.mapNotNull { id -> DemoCatalog.songs.find { it.id == id } }
        val client = provider.client ?: return emptyList()
        return client.from("playlist_songs").select {
            filter { eq("playlist_id", playlistId) }
        }.decodeList<PlaylistSongDto>().mapNotNull { row -> song(row.songId) }
    }

    private val remoteSongMetadata = mutableMapOf<String, SongDto>()

    private suspend fun fetchSongs(offset: Int, limit: Int): Result<List<Song>> = runCatching {
        val client = provider.client ?: error("Supabase is not configured")
        client.from("songs").select {
            range(offset.toLong()..(offset + limit - 1).toLong())
        }.decodeList<SongDto>().also { rows -> rows.forEach { remoteSongMetadata[it.id] = it } }.map(SongDto::toDomain)
    }

    private suspend fun fetchRemotePlaylists(): Result<List<MusicPlaylist>> = runCatching {
        val client = provider.client ?: error("Supabase is not configured")
        client.from("playlists").select().decodeList<PlaylistDto>()
            .map(PlaylistDto::toDomain)
            .ifEmpty { error("No remote playlists are configured") }
    }

    private inner class CatalogPagingSource(
        private val query: String,
        private val filter: CatalogFilter,
    ) : PagingSource<Int, Song>() {
        override fun getRefreshKey(state: PagingState<Int, Song>): Int? =
            state.anchorPosition?.let(state::closestPageToPosition)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
            val page = params.key ?: 0
            return try {
                val normalized = query.trim()
                val all = if (provider.isConfigured) {
                    fetchRemoteSearch(normalized, filter, page * params.loadSize, params.loadSize)
                } else {
                    filterLocal(DemoCatalog.songs, normalized, filter)
                        .drop(page * params.loadSize)
                        .take(params.loadSize)
                }
                LoadResult.Page(
                    data = all,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (all.size < params.loadSize) null else page + 1,
                )
            } catch (error: Throwable) {
                val fallback = filterLocal(DemoCatalog.songs, query.trim(), filter)
                    .drop(page * params.loadSize).take(params.loadSize)
                if (fallback.isNotEmpty() || page == 0) LoadResult.Page(
                    data = fallback,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (fallback.size < params.loadSize) null else page + 1,
                ) else LoadResult.Error(error)
            }
        }
    }

    private inner class PlaylistPagingSource(
        private val kind: PlaylistKind,
    ) : PagingSource<Int, MusicPlaylist>() {
        override fun getRefreshKey(state: PagingState<Int, MusicPlaylist>) =
            state.anchorPosition?.let(state::closestPageToPosition)?.let { it.prevKey?.plus(1) ?: it.nextKey?.minus(1) }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MusicPlaylist> {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            return runCatching {
                val data = provider.client?.from("playlists")?.select {
                    filter { eq("kind", kind.name) }
                    range(offset.toLong()..(offset + params.loadSize - 1).toLong())
                }?.decodeList<PlaylistDto>()?.map(PlaylistDto::toDomain)
                    ?: (DemoCatalog.playlists + localUserPlaylists).filter { it.kind == kind }
                        .drop(offset).take(params.loadSize)
                LoadResult.Page(data, if (page == 0) null else page - 1, if (data.size < params.loadSize) null else page + 1)
            }.getOrElse { LoadResult.Error(it) }
        }
    }

    private inner class PlaylistSongsPagingSource(
        private val playlistId: String,
    ) : PagingSource<Int, Song>() {
        override fun getRefreshKey(state: PagingState<Int, Song>) =
            state.anchorPosition?.let(state::closestPageToPosition)?.let { it.prevKey?.plus(1) ?: it.nextKey?.minus(1) }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
            val page = params.key ?: 0
            val offset = page * params.loadSize
            return runCatching {
                val local = (DemoCatalog.playlists + localUserPlaylists).find { it.id == playlistId }
                val data = if (local != null) {
                    local.songIds.drop(offset).take(params.loadSize).mapNotNull { id -> DemoCatalog.songs.find { it.id == id } }
                } else {
                    val client = provider.client
                    if (client == null) emptyList() else client.from("playlist_songs").select {
                            filter { eq("playlist_id", playlistId) }
                            range(offset.toLong()..(offset + params.loadSize - 1).toLong())
                        }.decodeList<PlaylistSongDto>().mapNotNull { song(it.songId) }
                }
                LoadResult.Page(data, if (page == 0) null else page - 1, if (data.size < params.loadSize) null else page + 1)
            }.getOrElse { LoadResult.Error(it) }
        }
    }

    private suspend fun fetchRemoteSearch(query: String, filter: CatalogFilter, offset: Int, limit: Int): List<Song> {
        val client = provider.client ?: error("Supabase is not configured")
        return client.from("songs").select {
            if (query.isNotBlank()) filter {
                when (filter) {
                    CatalogFilter.ALL -> or {
                        ilike("title", "%$query%")
                        ilike("artist_name", "%$query%")
                        ilike("album", "%$query%")
                    }
                    CatalogFilter.SONGS -> ilike("title", "%$query%")
                    CatalogFilter.ARTISTS -> ilike("artist_name", "%$query%")
                    CatalogFilter.ALBUMS -> ilike("album", "%$query%")
                }
            }
            range(offset.toLong()..(offset + limit - 1).toLong())
        }.decodeList<SongDto>().map(SongDto::toDomain)
    }

    private fun filterLocal(songs: List<Song>, query: String, filter: CatalogFilter): List<Song> {
        if (query.isBlank()) return songs
        return songs.filter { song ->
            when (filter) {
                CatalogFilter.ALL -> song.title.contains(query, true) || song.artistName.contains(query, true) || song.album.contains(query, true)
                CatalogFilter.SONGS -> song.title.contains(query, true)
                CatalogFilter.ARTISTS -> song.artistName.contains(query, true)
                CatalogFilter.ALBUMS -> song.album.contains(query, true)
            }
        }
    }

    private companion object { const val PAGE_SIZE = 20 }
}
