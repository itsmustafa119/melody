package com.mustafa.melody.data.mapper

import com.mustafa.melody.data.local.entity.SearchHistoryEntity
import com.mustafa.melody.domain.model.SearchHistoryItem

fun SearchHistoryEntity.toDomain() = SearchHistoryItem(
    query = query,
    searchedAt = searchedAt
)

fun SearchHistoryItem.toEntity() = SearchHistoryEntity(
    query = query,
    searchedAt = searchedAt
)
