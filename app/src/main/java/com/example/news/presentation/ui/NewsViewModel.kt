package com.example.news.presentation.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.news.NewsApplication
import com.example.news.common.networkResultHandler
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.repository.NewsRepository
import com.example.news.presentation.adapters.paging.NETWORK_PAGE_SIZE
import com.example.news.presentation.adapters.paging.NewsPagingSource
import com.example.news.presentation.adapters.paging.NewsRemoteMediator
import com.example.news.presentation.ui.fragments.breaking.BreakingNewsUiState
import com.example.news.presentation.ui.fragments.search_news.SearchNewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val application: NewsApplication,
    private val telephonyManager: TelephonyManager
) : ViewModel() {

    private val _breakingNews = MutableStateFlow(BreakingNewsUiState())
    val breakingNews get() = _breakingNews.asStateFlow()

    private val _searchNews = MutableStateFlow(SearchNewsUiState())
    val searchNews = _searchNews.asStateFlow()

    init {
    }

    private val _breakingNewsPaging = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { NewsPagingSource(newsRepository, "us") }
    ).flow.cachedIn(viewModelScope)

//        @OptIn(ExperimentalPagingApi::class)
//    private val _breakingNewsPaging = Pager(
//        config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
//        remoteMediator = NewsRemoteMediator(newsRepository, getSimCardCountryCode()),
//    ) {
//        newsRepository.getAllArticles()
//    }.flow
    val breakingNewsPaging get() = _breakingNewsPaging

    fun searchNews(
        query: String,
        category: String? = null,
        pageNumber: Int = 1
    ) {

        _searchNews.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                if (hasInternetConnection()) {
                    val response =
                        newsRepository.searchNews(query, category, pageNumber)
                    val result = networkResultHandler(response)
                    _searchNews.update { it.copy(data = result) }
                } else {
                    _searchNews.update { it.copy(message = "No internet connection") }
                }
            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _searchNews.update { it.copy(message = "No internet connection") }
                    else -> _searchNews.update { it.copy(message = "Something went wrong") }
                }
            } finally {
                _searchNews.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveArticle(article: EntitySavedArticle) =
        viewModelScope.launch { newsRepository.upsert(article) }

    fun deleteArticle(article: EntitySavedArticle) =
        viewModelScope.launch { newsRepository.deleteArticle(article) }

    fun getSavedNews() = newsRepository.getSavedNews().asLiveData()

    override fun onCleared() {
        super.onCleared()
        Log.i("news_vm", "onCleared called")
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun getSimCardCountryCode(): String {
        val countryCodeValue = telephonyManager.networkCountryIso
        Log.i("locale_activity", "country code: $countryCodeValue")
        return countryCodeValue
    }
}