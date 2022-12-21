package com.example.news.feature.saved_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.core.data.model.Article
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(private val newsUseCase: NewsUseCase) : ViewModel() {

    val savedNews: Flow<List<Article>> = newsUseCase.getAllSaveArticles()

    fun saveArticle(article: Article) =
        viewModelScope.launch { newsUseCase.upsertSavedArticle(article) }

    fun deleteArticle(articleUrl: String) =
        viewModelScope.launch { newsUseCase.deleteSavedArticle(articleUrl) }

}