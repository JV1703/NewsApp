package com.example.news.feature.search_news

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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchNewsViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_SEARCH_QUERY = "search_query"
    }

    val searchQuery = MutableStateFlow("")

    fun saveSearchQuery(searchQuery: String) {
        this@SearchNewsViewModel.searchQuery.value = searchQuery
        savedStateHandle[SAVED_SEARCH_QUERY] = this@SearchNewsViewModel.searchQuery.value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult = savedStateHandle.getStateFlow(key = SAVED_SEARCH_QUERY, "").debounce(500)
        .distinctUntilChanged()
        .filter { it.trim().isNotEmpty() }
        .flatMapLatest { searchNews(it) }

    fun searchNews(
        query: String,
    ): Flow<PagingData<Article>> = newsUseCase.searchNews(query).cachedIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        Log.i("news_vm", "onCleared called")
    }

}