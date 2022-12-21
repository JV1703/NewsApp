package com.example.news.feature.breaking_news

data class BreakingNewsUiState(
    val isLoading: Boolean = false,
    val message: String = "",
)