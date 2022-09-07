package com.example.news.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.network.models.NetworkArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: EntitySavedArticle): Long

    @Query("SELECT * FROM saved_articles")
    fun getAllSavedArticles(): Flow<List<EntitySavedArticle>>

    @Delete
    suspend fun deleteSavedArticle(article: EntitySavedArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArticles(articles: List<EntityArticles>)

    @Query("SELECT * FROM articles WHERE title LIKE :query ORDER BY publishedAt DESC")
    fun pagingSource(query: String): PagingSource<Int, EntityArticles>

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticle()

    @Query("SELECT * FROM articles")
    fun getAllArticles(): PagingSource<Int, NetworkArticle>

}