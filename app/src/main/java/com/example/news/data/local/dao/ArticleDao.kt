package com.example.news.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.local.entities.EntitySearchArticles
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    // Saved articles

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: EntitySavedArticle): Long

    @Query("SELECT * FROM saved_articles")
    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>>

    @Delete
    suspend fun deleteSavedArticle(article: EntitySavedArticle)

    // Breaking News Remote Mediator

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<EntityArticles>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticle()

    @Query("SELECT * FROM articles")
    fun getAllArticles(): PagingSource<Int, EntityArticles>

    // Search Remote Mediator

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSearchArticles(articles: List<EntitySearchArticles>)

    @Query("DELETE FROM search_articles")
    suspend fun deleteAllSearchedArticles()

    @Query("SELECT * FROM search_articles WHERE title LIKE  '%' || :query || '%'")
    fun searchArticles(query: String): PagingSource<Int, EntitySearchArticles>

}