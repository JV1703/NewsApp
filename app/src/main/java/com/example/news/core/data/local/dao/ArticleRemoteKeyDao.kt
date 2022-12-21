package com.example.news.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.news.core.data.local.entities.EntityArticleRemoteKey

@Dao
interface ArticleRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<EntityArticleRemoteKey>)

    @Query("SELECT * FROM article_remote_keys WHERE id = :id")
    suspend fun remoteKeysRepoId(
        id: String,
    ): EntityArticleRemoteKey?

    @Query("DELETE FROM article_remote_keys")
    suspend fun clearRemoteKeys()

}