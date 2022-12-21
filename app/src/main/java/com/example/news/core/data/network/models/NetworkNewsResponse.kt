package com.example.news.core.data.network.models


import com.example.news.core.data.model.News
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkNewsResponse(
    @Json(name = "articles")
    val networkArticles: List<NetworkArticle>,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int,
)

fun NetworkNewsResponse.toNews() = News(
    networkArticles = networkArticles.map { it.toArticle() },
    status = status,
    totalResults = totalResults
)