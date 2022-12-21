package com.example.news.core.di.app

import android.content.Context
import android.telephony.TelephonyManager
import com.example.news.NewsApplication
import com.example.news.core.common.DevCredentials
import com.example.news.core.common.network_connectivity_observer.ConnectivityObserver
import com.example.news.core.common.network_connectivity_observer.NetworkConnectivityObserver
import com.example.news.core.data.local.ArticleDatabase
import com.example.news.core.data.local.dao.ArticleDao
import com.example.news.core.data.local.dao.ArticleRemoteKeyDao
import com.example.news.core.data.network.NewsApi
import com.example.news.core.repository.NewsRepository
import com.example.news.feature.common.domain.abstraction.NewsUseCase
import com.example.news.feature.common.domain.implementation.NewsUseCaseImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var original = chain.request()
            val token = DevCredentials.API_KEY
            val url = original.url.newBuilder().addQueryParameter("apiKey", token).build()
            original = original.newBuilder().url(url).build()
            return chain.proceed(original)
        }
    }

    @Provides
    @Singleton
    fun apiKeyInterceptor() = ApiKeyInterceptor()

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun okHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .readTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun moshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    fun newsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

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