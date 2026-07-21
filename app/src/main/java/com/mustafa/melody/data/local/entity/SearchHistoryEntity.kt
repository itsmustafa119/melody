package com.mustafa.melody.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [
        Index(value = ["searched_at"])
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey
    @ColumnInfo(
        name = "query",
        collate = ColumnInfo.NOCASE
    )
    val query: String,

    @ColumnInfo(name = "searched_at")
    val searchedAt: Long
)
