package com.example.news.di

import com.example.news.core.test.FakeDataGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TestModule {

    @Provides
    fun provideFakeDataGenerator(): FakeDataGenerator = FakeDataGenerator()

}