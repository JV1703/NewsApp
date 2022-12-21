package com.example.news.di

import com.example.news.core.di.app.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
object TestNetworkModule {

    @Provides
    @Singleton
    fun retrofit(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder().addConverterFactory(moshiConverterFactory).client(okHttpClient)
        .baseUrl("https://6ddb0638-87a6-464e-88ee-2d99998bb0ab.mock.pstmn.io").build()

}