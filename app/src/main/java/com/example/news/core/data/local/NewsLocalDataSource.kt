package com.example.news.core.data.local

import androidx.paging.PagingSource
import com.example.news.core.data.local.dao.ArticleDao
import com.example.news.core.data.local.dao.ArticleRemoteKeyDao
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntityArticleRemoteKey
import com.example.news.core.data.local.entities.EntitySavedArticle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsLocalDataSource @Inject constructor(
    private val articleDao: ArticleDao,
    private val articleRemoteKeyDao: ArticleRemoteKeyDao,
) {

    suspend fun upsertSaveArticle(article: EntitySavedArticle) =
        articleDao.upsertSavedArticle(article)

    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>> = articleDao.getAllSavedArticles()

    fun getSavedArticle(url: String): Flow<EntitySavedArticle?> = articleDao.getSavedArticle(url)

    suspend fun deleteSavedArticle(articleUrl: String) =
        articleDao.deleteSavedArticle(articleUrl)

    suspend fun insertAllArticles(articles: List<EntityArticle>) =
        articleDao.insertAllArticles(articles)

    fun getAllArticles(): PagingSource<Int, EntityArticle> = articleDao.getAllArticles()

    suspend fun deleteAllArticles() = articleDao.deleteAllArticle()

    suspend fun insertAllRemoteKey(remoteKey: List<EntityArticleRemoteKey>) =
        articleRemoteKeyDao.insertAllRemoteKeys(remoteKey)

    suspend fun remoteKeysArticleId(
        id: String,
    ): EntityArticleRemoteKey? = articleRemoteKeyDao.remoteKeysRepoId(id)

    suspend fun clearRemoteKeys() =
        articleRemoteKeyDao.clearRemoteKeys()

}