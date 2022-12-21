package com.example.news.core.data.model

import android.os.Parcelable
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntitySavedArticle
import com.example.news.core.data.network.models.Source
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String,
    val source: @RawValue Source,
    val title: String,
    val url: String,
    val urlToImage: String?,
) : Parcelable

fun Article.toEntitySavedArticle() = EntitySavedArticle(author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage)

fun Article.toEntityArticle() = EntityArticle(author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage)
