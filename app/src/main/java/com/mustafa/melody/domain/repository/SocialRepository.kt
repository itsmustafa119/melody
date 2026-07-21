package com.mustafa.melody.domain.repository

import com.mustafa.melody.domain.model.SocialProfile
import com.mustafa.melody.domain.model.MusicPlaylist

interface SocialRepository {
    suspend fun searchUsers(query: String): List<SocialProfile>
    suspend fun followingIds(): Set<String>
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
    suspend fun publicPlaylists(userId: String): List<MusicPlaylist>
}
