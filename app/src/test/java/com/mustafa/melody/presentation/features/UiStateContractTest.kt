package com.mustafa.melody.presentation.features

import com.mustafa.melody.presentation.home.HomeUiState
import com.mustafa.melody.presentation.search.SearchUiState
import com.mustafa.melody.presentation.search.SearchFilter
import com.mustafa.melody.presentation.downloads.DownloadsUiState
import com.mustafa.melody.presentation.playlists.PlaylistsUiState
import com.mustafa.melody.presentation.playlists.PlaylistSection
import com.mustafa.melody.presentation.profile.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UiStateContractTest {

    @Test
    fun `HomeUiState default values should be correct`() {
        val state = HomeUiState()
        assertTrue(state.isLoading)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `SearchUiState default values should be correct`() {
        val state = SearchUiState()
        assertEquals("", state.query)
        assertEquals(SearchFilter.ALL, state.selectedFilter)
        assertTrue(state.searchHistory.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `DownloadsUiState default values should be correct`() {
        val state = DownloadsUiState()
        assertFalse(state.isLoading)
        assertEquals(0, state.downloadedSongCount)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `PlaylistsUiState default values should be correct`() {
        val state = PlaylistsUiState()
        assertTrue(state.isLoading)
        assertEquals(PlaylistSection.WORLD, state.selectedSection)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `ProfileUiState default values should be correct`() {
        val state = ProfileUiState()
        assertEquals("", state.displayName)
        assertFalse(state.isPremium)
        assertFalse(state.isLoading)
        assertNull(state.errorMessageResId)
    }
}
