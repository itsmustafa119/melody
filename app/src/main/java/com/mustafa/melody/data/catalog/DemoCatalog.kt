package com.mustafa.melody.data.catalog

import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.PlaylistKind
import com.mustafa.melody.domain.model.Song

/** Offline-first presentation data used when Supabase credentials are not configured. */
object DemoCatalog {
    private val artists = listOf(
        "Aria Nova", "Caspian Echo", "Lena Hart", "Neon Atlas", "Darya", "The Daylights",
        "Soroush Waves", "Mira Lane", "Northbound", "Raha", "Velvet Transit", "Kian Mehr",
    )
    private val titles = listOf(
        "Afterglow", "Blue Horizon", "City Lights", "Drift", "Electric Rain", "Far Away",
        "Golden Hour", "Home Again", "Infinite Road", "Jasmine Sky", "Keep Moving", "Lost Stars",
        "Midnight Drive", "New Beginning", "Ocean Heart", "Paper Planes", "Quiet Fire", "Runaway",
        "Silver Lines", "Tonight", "Under the Moon", "Velvet Morning", "Wildflower", "Your Echo", "Zenith",
    )

    val songs: List<Song> = List(50) { index ->
        val number = index + 1
        Song(
            id = "song-$number",
            title = titles[index % titles.size] + if (index >= titles.size) " II" else "",
            artistName = artists[index % artists.size],
            coverImageUrl = "https://picsum.photos/seed/melody-$number/600/600",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${index % 16 + 1}.mp3",
            album = if (index % 3 == 0) "Persian Nights" else if (index % 3 == 1) "Global Pulse" else "Fresh Finds",
            isLocal = index % 3 == 0,
        )
    }

    val playlists: List<MusicPlaylist> = List(12) { index ->
        val kind = PlaylistKind.entries[index % PlaylistKind.entries.size]
        MusicPlaylist(
            id = "playlist-${index + 1}",
            title = when (kind) {
                PlaylistKind.WORLD -> "World Mix ${index / 3 + 1}"
                PlaylistKind.LOCAL -> "Persian Essentials ${index / 3 + 1}"
                PlaylistKind.USER -> "My Collection ${index / 3 + 1}"
            },
            subtitle = "${10 + index} songs",
            coverImageUrl = "https://picsum.photos/seed/playlist-$index/600/600",
            songIds = songs.drop(index * 3).take(12).map(Song::id),
            kind = kind,
        )
    }
}
