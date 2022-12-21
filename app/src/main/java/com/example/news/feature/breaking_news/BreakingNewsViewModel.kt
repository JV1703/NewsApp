package com.example.news.feature.breaking_news

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BreakingNewsViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
) : ViewModel() {

    private val _breakingNews = MutableStateFlow(BreakingNewsUiState())
    val breakingNews get() = _breakingNews.asStateFlow()

    val breakingNewsPaging = newsUseCase.getBreakingNews()

    override fun onCleared() {
        super.onCleared()
        Log.i("news_vm", "onCleared called")
    }

}