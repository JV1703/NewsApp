package com.example.news.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntitySavedArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    // Saved articles

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedArticle(article: EntitySavedArticle)

    @Query("SELECT * FROM saved_articles")
    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>>

    @Query("SELECT * FROM saved_articles WHERE url = :url")
    fun getSavedArticle(url: String): Flow<EntitySavedArticle?>

    @Query("DELETE FROM saved_articles WHERE url = :articleUrl")
    suspend fun deleteSavedArticle(articleUrl: String)

    // Breaking News Remote Mediator

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<EntityArticle>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticle()

    @Query("SELECT * FROM articles")
    fun getAllArticles(): PagingSource<Int, EntityArticle>

    @Query("SELECT * FROM articles")
    fun getAllEntityArticles(): Flow<List<EntityArticle>>

}