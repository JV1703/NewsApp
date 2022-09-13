package com.example.news.data.repository

import androidx.paging.PagingSource
import com.example.news.common.Constants
import com.example.news.data.data_source.NewsLocalDataSource
import com.example.news.data.data_source.NewsRemoteDataSource
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.local.entities.EntitySearchArticles
import javax.inject.Inject

class NewsRepository @Inject constructor(
    val local: NewsLocalDataSource,
    val remote: NewsRemoteDataSource
) {

    suspend fun getBreakingNews(
        countryCode: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20
    ) =
        remote.getNews(countryCode, category, page = page, pageSize = pageSize)

    suspend fun searchNews(
        query: String,
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20,
        sortBy: String? = null /*relevancy, popularity, publishedAt*/
    ) =
        remote.searchNews(query, category, page, pageSize)

    suspend fun upsert(article: EntitySavedArticle) = local.upsert(article)

    fun getSavedNews() = local.getAllSavedArticles()

    suspend fun deleteArticle(article: EntitySavedArticle) = local.deleteArticle(article)

    suspend fun insertAllArticles(articles: List<EntityArticles>) =
        local.insertAllArticles(articles)

    suspend fun deleteAllArticle() = local.deleteAllArticles()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        local.insertAllRemoteKey(remoteKey)

    suspend fun remoteKeysArticleId(
        category: Constants.KeyCategory,
        id: String
    ): EntityArticleRemoteKey? =
        local.remoteKeysArticleId(category, id)

    suspend fun clearRemoteKeys(category: Constants.KeyCategory) = local.clearRemoteKeys(category)

    fun getAllArticles(): PagingSource<Int, EntityArticles> = local.getAllArticles()

    suspend fun insertAllSearchArticles(articles: List<EntitySearchArticles>) =
        local.insertAllSearchArticles(articles)

    suspend fun deleteAllSearchedArticles() = local.deleteAllSearchedArticles()

    fun searchArticles(query: String): PagingSource<Int, EntitySearchArticles> =
        local.searchArticles(query)

}