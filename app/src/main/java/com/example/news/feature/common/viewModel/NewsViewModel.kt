package com.example.news.feature.common.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.news.core.data.model.Article
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_SEARCH_QUERY = "search_query"
    }

    init {
        Log.i("NewsViewModel", "initialized")
    }

    val breakingNewsPaging = newsUseCase.getBreakingNews()
    private val searchQuery = MutableStateFlow("")
    val savedNews: Flow<List<Article>> = newsUseCase.getAllSaveArticles()

    fun saveSearchQuery(searchQuery: String) {
        this@NewsViewModel.searchQuery.value = searchQuery
        savedStateHandle[SAVED_SEARCH_QUERY] = this@NewsViewModel.searchQuery.value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult =
        searchQuery.debounce(500).distinctUntilChanged().filter { it.trim().isNotEmpty() }
            .flatMapLatest { searchNews(it) }

    fun searchNews(
        query: String,
    ): Flow<PagingData<Article>> = newsUseCase.searchNews(query).cachedIn(viewModelScope)

    fun saveArticle(article: Article) =
        viewModelScope.launch { newsUseCase.upsertSavedArticle(article) }

    fun deleteArticle(articleUrl: String) =
        viewModelScope.launch { newsUseCase.deleteSavedArticle(articleUrl) }

    override fun onCleared() {
        super.onCleared()
    }

}