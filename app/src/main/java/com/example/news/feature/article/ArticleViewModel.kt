package com.example.news.feature.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.core.data.model.Article
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(private val newsUseCase: NewsUseCase) : ViewModel() {

    fun saveArticle(article: Article) {
        viewModelScope.launch { newsUseCase.upsertSavedArticle(article) }
    }

    fun deleteArticle(articleUrl: String) {
        viewModelScope.launch { newsUseCase.deleteSavedArticle(articleUrl) }
    }

    fun getSavedArticle(url: String) = newsUseCase.getSavedArticle(url)

}