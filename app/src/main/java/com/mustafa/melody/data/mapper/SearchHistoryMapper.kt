package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import com.mustafa.melody.domain.model.SearchHistoryItem

fun SearchHistoryEntity.toDomain() = SearchHistoryItem(
    query = searchQuery,
    searchedAt = searchedAt
)

fun SearchHistoryItem.toEntity() = SearchHistoryEntity(
    searchQuery = query,
    searchedAt = searchedAt
)
