package com.example.news.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.news.common.Constants
import com.example.news.data.local.entities.EntityArticleRemoteKey

@Dao
interface ArticleRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<EntityArticleRemoteKey>)

    @Query("SELECT * FROM article_remote_keys WHERE category = :category AND id = :id")
    suspend fun remoteKeysRepoId(
        category: Constants.KeyCategory,
        id: String
    ): EntityArticleRemoteKey?

    @Query("DELETE FROM article_remote_keys WHERE category = :category")
    suspend fun clearRemoteKeys(category: Constants.KeyCategory)

}