package com.example.news.data.data_source

import androidx.paging.PagingSource
import androidx.room.Query
import com.example.news.common.Constants
import com.example.news.data.local.dao.ArticleDao
import com.example.news.data.local.dao.ArticleRemoteKeyDao
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.local.entities.EntitySearchArticles
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsLocalDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val articleRemoteKeyDao: ArticleRemoteKeyDao
) {

    suspend fun upsert(article: EntitySavedArticle) = articleDao.upsert(article)

    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>> = articleDao.getAllSavedArticles()

    suspend fun deleteArticle(article: EntitySavedArticle) = articleDao.deleteSavedArticle(article)

    suspend fun insertAllArticles(articles: List<EntityArticles>) =
        articleDao.insertAllArticles(articles)

    suspend fun deleteAllArticles() = articleDao.deleteAllArticle()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        articleRemoteKeyDao.insertAllRemoteKeys(remoteKey)

    suspend fun remoteKeysArticleId(
        category: Constants.KeyCategory,
        id: String
    ): EntityArticleRemoteKey? =
        articleRemoteKeyDao.remoteKeysRepoId(category, id)

    suspend fun clearRemoteKeys(category: Constants.KeyCategory) = articleRemoteKeyDao.clearRemoteKeys(category)

    fun getAllArticles(): PagingSource<Int, EntityArticles> = articleDao.getAllArticles()

    suspend fun insertAllSearchArticles(articles: List<EntitySearchArticles>) = articleDao.insertAllSearchArticles(articles)

    suspend fun deleteAllSearchedArticles() = articleDao.deleteAllSearchedArticles()

    fun searchArticles(query: String): PagingSource<Int, EntitySearchArticles> = articleDao.searchArticles(query)

}