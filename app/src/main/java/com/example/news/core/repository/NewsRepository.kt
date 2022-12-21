package com.example.news.core.repository

import androidx.paging.PagingSource
import com.example.movies.common.di.CoroutinesQualifiers
import com.example.news.core.common.safeNetworkCall
import com.example.news.core.data.local.NewsLocalDataSource
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntityArticleRemoteKey
import com.example.news.core.data.local.entities.toArticle
import com.example.news.core.data.model.Article
import com.example.news.core.data.model.toEntityArticle
import com.example.news.core.data.model.toEntitySavedArticle
import com.example.news.core.data.network.NewsNetworkDataSource
import com.example.news.core.data.network.models.toNews
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val local: NewsLocalDataSource,
    private val remote: NewsNetworkDataSource,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getBreakingNews(
        countryCode: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20,
    ) = safeNetworkCall(ioDispatcher,
        networkCall = {
            remote.getNews(countryCode = countryCode,
                category = category,
                page = page,
                pageSize = pageSize)
        },
        conversion = { networkNewsResponse ->
            networkNewsResponse.toNews()
        })

    suspend fun searchNews(
        query: String,
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20,
        sortBy: String? = null, /*relevancy, popularity, publishedAt*/
    ) = safeNetworkCall(ioDispatcher,
        networkCall = {
            remote.searchNews(query = query,
                category = category,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy)
        },
        conversion = { networkNewsResponse -> networkNewsResponse.toNews() })

    suspend fun upsertSavedArticle(article: Article) =
        local.upsertSaveArticle(article.toEntitySavedArticle())

    fun getSavedArticles(): Flow<List<Article>> =
        local.getAllSavedArticles().map { it.map { it.toArticle() } }

    fun getSavedArticle(url: String): Flow<Article?> =
        local.getSavedArticle(url).map { it?.toArticle() }

    suspend fun deleteSavedArticle(articleUrl: String) =
        local.deleteSavedArticle(articleUrl)

    suspend fun insertAllArticles(articles: List<Article>) =
        local.insertAllArticles(articles.map { it.toEntityArticle() })

    suspend fun deleteAllArticle() = local.deleteAllArticles()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        local.insertAllRemoteKey(remoteKey)

    suspend fun remoteKeysArticleId(
        id: String,
    ): EntityArticleRemoteKey? = local.remoteKeysArticleId(id)

    suspend fun clearRemoteKeys() =
        local.clearRemoteKeys()

    fun getAllArticles(): PagingSource<Int, EntityArticle> = local.getAllArticles()

}