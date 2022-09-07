package com.example.news.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_remote_keys")
data class EntityArticleRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prev: Int?,
    val next: Int?
)