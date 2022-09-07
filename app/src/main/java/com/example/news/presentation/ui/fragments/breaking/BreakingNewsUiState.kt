package com.example.news.presentation.ui.fragments.breaking

import com.example.news.common.NetworkResult
import com.example.news.data.network.models.NetworkNewsResponse

data class BreakingNewsUiState(
    val isLoading: Boolean = false,
    val data: NetworkResult<NetworkNewsResponse>? = null,
    val message: String = ""
)