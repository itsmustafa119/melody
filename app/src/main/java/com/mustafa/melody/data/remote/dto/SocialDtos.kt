package com.mustafa.melody.data.remote.dto

import com.mustafa.melody.domain.model.SocialProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val bio: String = "",
    @SerialName("is_premium") val isPremium: Boolean = false,
) {
    fun toDomain() = SocialProfile(id, username, displayName, avatarUrl, bio, isPremium)
}

@Serializable
data class FollowDto(
    @SerialName("follower_id") val followerId: String,
    @SerialName("followed_id") val followedId: String,
)
