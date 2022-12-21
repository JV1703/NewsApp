package com.example.news.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.news.core.data.local.dao.ArticleDao
import com.example.news.core.data.local.dao.ArticleRemoteKeyDao
import com.example.news.core.data.local.entities.EntityArticle
import com.example.news.core.data.local.entities.EntityArticleRemoteKey
import com.example.news.core.data.local.entities.EntitySavedArticle
import com.example.news.core.data.local.entities.EntitySearchArticles
import com.example.news.core.data.local.type_converter.TypeConverter

@Database(
    entities = [EntitySavedArticle::class, EntityArticle::class, EntityArticleRemoteKey::class, EntitySearchArticles::class],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleRemoteKeyDao(): ArticleRemoteKeyDao
}