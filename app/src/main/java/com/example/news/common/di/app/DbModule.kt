package com.example.news.common.di.app

import android.content.Context
import androidx.room.Room
import com.example.news.data.local.ArticleDatabase
import com.example.news.data.local.dao.ArticleDao
import com.example.news.data.local.dao.ArticleRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ArticleDatabase::class.java, "news_database").build()

    @Singleton
    @Provides
    fun provideArticleDao(articleDatabase: ArticleDatabase): ArticleDao =
        articleDatabase.articleDao()

    @Singleton
    @Provides
    fun provideArticleRemoteKeyDao(articleDatabase: ArticleDatabase): ArticleRemoteKeyDao =
        articleDatabase.articleRemoteKeyDao()

}