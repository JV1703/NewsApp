package com.example.news.data.data_source

import androidx.paging.PagingSource
import com.example.news.data.local.dao.ArticleDao
import com.example.news.data.local.dao.ArticleRemoteKeyDao
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.network.models.NetworkArticle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsLocalDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val articleRemoteKeyDao: ArticleRemoteKeyDao
) {

    suspend fun upsert(article: EntitySavedArticle) = articleDao.upsert(article)

    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>> = articleDao.getAllSavedArticles()

    suspend fun deleteArticle(article: EntitySavedArticle) = articleDao.deleteSavedArticle(article)

    suspend fun insertAllArticles(articles: List<EntityArticles>) = articleDao.insertAllArticles(articles)

    fun pagingSource(query: String): PagingSource<Int, EntityArticles> =
        articleDao.pagingSource(query)

    suspend fun deleteAllArticles() = articleDao.deleteAllArticle()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        articleRemoteKeyDao.insertAllRemoteKeys(remoteKey)

    suspend fun remoteKeysArticleId(id: String): EntityArticleRemoteKey? =
        articleRemoteKeyDao.remoteKeysRepoId(id)

    suspend fun clearRemoteKeys() = articleRemoteKeyDao.clearRemoteKeys()

    fun getAllArticles(): PagingSource<Int, NetworkArticle> = articleDao.getAllArticles()

}