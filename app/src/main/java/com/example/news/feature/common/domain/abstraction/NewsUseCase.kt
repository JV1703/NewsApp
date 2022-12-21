package com.example.news.feature.common.domain.abstraction

import androidx.paging.PagingData
import com.example.news.core.data.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsUseCase {

    fun getBreakingNews(
        countryCode: String = "us",
        category: String? = null,
    ): Flow<PagingData<Article>>

    fun searchNews(
        query: String,
        category: String? = null,
        sortBy: String? = null,
    ): Flow<PagingData<Article>>

    suspend fun upsertSavedArticle(article: Article)
    suspend fun deleteSavedArticle(articleUrl: String)
    fun getAllSaveArticles(): Flow<List<Article>>
    fun getSavedArticle(url: String): Flow<Article?>
}