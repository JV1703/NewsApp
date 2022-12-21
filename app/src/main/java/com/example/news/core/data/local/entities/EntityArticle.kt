package com.example.news.core.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.news.core.data.model.Article
import com.example.news.core.data.network.models.NetworkArticle
import com.example.news.core.data.network.models.Source
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "articles")
data class EntityArticle(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String,
    val source: @RawValue Source,
    val title: String,
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val urlToImage: String?,
) : Parcelable

fun EntityArticle.toArticle() = Article(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage
)

fun EntityArticle.asNetworkArticles() = NetworkArticle(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage
)