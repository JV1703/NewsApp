package com.example.news.data.data_source

import com.example.movies.common.di.CoroutinesQualifiers
import com.example.news.data.network.NewsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRemoteDataSource @Inject constructor(
    private val api: NewsApi,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getNews(
        countryCode: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20
    ) = withContext(ioDispatcher) { api.getNews(countryCode, category, page, pageSize) }

    suspend fun searchNews(
        query: String,
        category: String? = null,
        pageNumber: Int = 1
    ) = withContext(ioDispatcher) { api.searchForNews(query, category, pageNumber) }

}