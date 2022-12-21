package com.example.news.core.data.model

data class News(
    val networkArticles: List<Article>,
    val status: String,
    val totalResults: Int,
)