package com.mustafa.melody.data.repository

import com.mustafa.melody.data.remote.SupabaseProvider
import com.mustafa.melody.data.remote.dto.FollowDto
import com.mustafa.melody.data.remote.dto.ProfileDto
import com.mustafa.melody.data.remote.dto.PlaylistDto
import com.mustafa.melody.data.catalog.DemoCatalog
import com.mustafa.melody.domain.model.MusicPlaylist
import com.mustafa.melody.domain.model.SocialProfile
import com.mustafa.melody.domain.repository.SocialRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val provider: SupabaseProvider,
) : SocialRepository {
    private val localFollowing = mutableSetOf<String>()
    private val demoProfiles = listOf(
        SocialProfile("demo-user-1", "sara", "Sara Music", null, "Indie and Persian pop", false),
        SocialProfile("demo-user-2", "aliwaves", "Ali Waves", null, "Electronic listener", true),
        SocialProfile("demo-user-3", "nika", "Nika", null, "Always discovering", false),
        SocialProfile("demo-user-4", "rezabeats", "Reza Beats", null, "Producer", true),
        SocialProfile("demo-user-5", "minah", "Mina Hart", null, "Acoustic favorites", false),
        SocialProfile("demo-user-6", "arman", "Arman", null, "Classics and jazz", false),
    )

    override suspend fun searchUsers(query: String): List<SocialProfile> {
        val client = provider.client ?: return filterDemo(query)
        return runCatching {
            val currentId = client.auth.currentUserOrNull()?.id
            client.from("profiles").select {
                if (query.isNotBlank()) filter {
                    or {
                        ilike("display_name", "%$query%")
                        ilike("username", "%$query%")
                    }
                }
                limit(50)
            }.decodeList<ProfileDto>().filterNot { it.id == currentId }.map(ProfileDto::toDomain)
        }.getOrElse { filterDemo(query) }
    }

    override suspend fun followingIds(): Set<String> {
        val client = provider.client ?: return localFollowing.toSet()
        val currentId = client.auth.currentUserOrNull()?.id ?: return emptySet()
        return runCatching {
            client.from("follows").select {
                filter { eq("follower_id", currentId) }
            }.decodeList<FollowDto>().mapTo(mutableSetOf()) { it.followedId }
        }.getOrElse { localFollowing.toSet() }
    }

    override suspend fun follow(userId: String) {
        val client = provider.client
        val currentId = client?.auth?.currentUserOrNull()?.id
        if (client == null || currentId == null) localFollowing += userId
        else client.from("follows").upsert(FollowDto(currentId, userId))
    }

    override suspend fun unfollow(userId: String) {
        val client = provider.client
        val currentId = client?.auth?.currentUserOrNull()?.id
        if (client == null || currentId == null) localFollowing -= userId
        else client.from("follows").delete {
            filter {
                eq("follower_id", currentId)
                eq("followed_id", userId)
            }
        }
    }

    override suspend fun publicPlaylists(userId: String): List<MusicPlaylist> {
        val client = provider.client ?: return DemoCatalog.playlists.filter { it.kind.name == "USER" }.take(2)
        return runCatching {
            client.from("playlists").select {
                filter {
                    eq("owner_id", userId)
                    eq("is_public", true)
                }
                limit(20)
            }.decodeList<PlaylistDto>().map(PlaylistDto::toDomain)
        }.getOrElse { emptyList() }
    }

    private fun filterDemo(query: String) = demoProfiles.filter {
        query.isBlank() || it.displayName.contains(query, true) || it.username.contains(query, true)
    }
}
