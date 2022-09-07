package com.example.news.data.repository

import androidx.paging.PagingSource
import com.example.news.data.data_source.NewsLocalDataSource
import com.example.news.data.data_source.NewsRemoteDataSource
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.network.models.NetworkArticle
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

    suspend fun searchNews(query: String, category: String? = null, pageNumber: Int = 1) =
        remote.searchNews(query, category, pageNumber)

    suspend fun upsert(article: EntitySavedArticle) = local.upsert(article)

    fun getSavedNews() = local.getAllSavedArticles()

    suspend fun deleteArticle(article: EntitySavedArticle) = local.deleteArticle(article)

    suspend fun insertAllArticles(articles: List<EntityArticles>) =
        local.insertAllArticles(articles)

    fun pagingSource(query: String): PagingSource<Int, EntityArticles> = local.pagingSource(query)

    suspend fun deleteAllArticle() = local.deleteAllArticles()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        local.insertAllRemoteKey(remoteKey)

    suspend fun remoteKeysArticleId(id: String): EntityArticleRemoteKey? =
        local.remoteKeysArticleId(id)

    suspend fun clearRemoteKeys() = local.clearRemoteKeys()

    fun getAllArticles(): PagingSource<Int, NetworkArticle> = local.getAllArticles()

}