package com.example.news.feature.common.domain.implementation

import android.telephony.TelephonyManager
import android.util.Log
import androidx.paging.*
import com.example.news.core.data.local.entities.toArticle
import com.example.news.core.data.model.Article
import com.example.news.core.repository.NewsRepository
import com.example.news.feature.adapters.paging.BreakingNewsRemoteMediator
import com.example.news.feature.adapters.paging.SearchNewsPagingSource
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsUseCaseImpl @Inject constructor(
    private val newsRepository: NewsRepository,
    private val telephonyManager: TelephonyManager,
) : NewsUseCase {

    companion object {
        const val PAGING_SIZE = 10
    }

    private fun getSimCardCountryCode(): String {
        val countryCodeValue = telephonyManager.networkCountryIso
        Log.i("locale_activity", "country code: $countryCodeValue")
        return countryCodeValue
    }

    override fun getBreakingNews(
        countryCode: String,
        category: String?,
    ): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = PAGING_SIZE),
        remoteMediator = BreakingNewsRemoteMediator(repository = newsRepository,
            countryCode = getSimCardCountryCode(),
            category = category),
    ) {
        newsRepository.getAllArticles()
    }.flow.map { pagingData ->
        pagingData.map {
            it.toArticle()
        }
    }

    override fun searchNews(
        query: String,
        category: String?,
        sortBy: String?,
    ): Flow<PagingData<Article>> =
        Pager(config = PagingConfig(pageSize = PAGING_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchNewsPagingSource(
                    query = query,
                    repository = newsRepository,
                    category = category,
                    sortBy = sortBy)
            }).flow

    override suspend fun upsertSavedArticle(article: Article) {
        newsRepository.upsertSavedArticle(article)
    }

    override suspend fun deleteSavedArticle(articleUrl: String) {
        newsRepository.deleteSavedArticle(articleUrl)
    }

    override fun getAllSaveArticles(): Flow<List<Article>> = newsRepository.getSavedArticles()

    override fun getSavedArticle(url: String): Flow<Article?> = newsRepository.getSavedArticle(url)

}