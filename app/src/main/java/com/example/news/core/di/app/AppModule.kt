package com.example.news.core.di.app

import android.content.Context
import android.telephony.TelephonyManager
import com.example.news.NewsApplication
import com.example.news.core.common.network_connectivity_observer.ConnectivityObserver
import com.example.news.core.common.network_connectivity_observer.NetworkConnectivityObserver
import com.example.news.core.data.local.ArticleDatabase
import com.example.news.core.data.local.dao.ArticleDao
import com.example.news.core.data.local.dao.ArticleRemoteKeyDao
import com.example.news.core.repository.NewsRepository
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import com.example.news.feature.common.domain.implementation.NewsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): NewsApplication =
        (app as NewsApplication)

    @Provides
    fun provideTelephonyManager(@ApplicationContext app: Context): TelephonyManager =
        app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Singleton
    @Provides
    fun provideArticleDao(articleDatabase: ArticleDatabase): ArticleDao =
        articleDatabase.articleDao()

    @Singleton
    @Provides
    fun provideArticleRemoteKeyDao(articleDatabase: ArticleDatabase): ArticleRemoteKeyDao =
        articleDatabase.articleRemoteKeyDao()

    @Singleton
    @Provides
    fun provideNewsUseCase(
        newsRepository: NewsRepository,
        telephonyManager: TelephonyManager,
    ): NewsUseCase =
        NewsUseCaseImpl(newsRepository = newsRepository, telephonyManager = telephonyManager)

    @Singleton
    @Provides
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver =
        NetworkConnectivityObserver(context)

}