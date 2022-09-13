package com.example.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.news.data.local.dao.ArticleDao
import com.example.news.data.local.dao.ArticleRemoteKeyDao
import com.example.news.data.local.entities.EntityArticleRemoteKey
import com.example.news.data.local.entities.EntityArticles
import com.example.news.data.local.entities.EntitySavedArticle
import com.example.news.data.local.entities.EntitySearchArticles
import com.example.news.data.local.type_converter.TypeConverter

@Database(
    entities = [EntitySavedArticle::class, EntityArticles::class, EntityArticleRemoteKey::class, EntitySearchArticles::class],
    version = 1
)
@TypeConverters(TypeConverter::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleRemoteKeyDao(): ArticleRemoteKeyDao
}