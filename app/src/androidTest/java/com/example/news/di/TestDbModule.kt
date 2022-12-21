package com.example.news.di

import android.content.Context
import androidx.room.Room
import com.example.news.core.data.local.ArticleDatabase
import com.example.news.core.di.app.DbModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.mockwebserver.MockWebServer

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DbModule::class])
object TestDbModule {

    @Provides
    fun provideInMemoryTodoDb(@ApplicationContext context: Context): ArticleDatabase {
        return Room.inMemoryDatabaseBuilder(context, ArticleDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @Provides
    fun mockWebServer(): MockWebServer = MockWebServer()

}