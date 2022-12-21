package com.example.news.core.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.news.core.data.model.Article
import com.example.news.core.data.network.models.Source
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "saved_articles")
data class EntitySavedArticle(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null, /*set to null because we are not going to save all the articles*/
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String,
    val source: @RawValue Source,
    val title: String,
    val url: String,
    val urlToImage: String?,
) : Parcelable

fun EntitySavedArticle.toArticle() = Article(
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = source,
    title = title,
    url = url,
    urlToImage = urlToImage
)