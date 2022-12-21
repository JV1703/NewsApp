package com.example.news.core.data.network

import com.example.news.core.data.network.models.NetworkNewsResponse
import retrofit2.Response
import javax.inject.Inject

class NewsNetworkDataSource @Inject constructor(
    private val api: NewsApi,
) {

    suspend fun getNews(
        countryCode: String = "us",
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20,
    ): Response<NetworkNewsResponse> {
        return api.getNews(countryCode, category, page, pageSize)
    }

    suspend fun searchNews(
        query: String,
        category: String? = null,
        page: Int = 1,
        pageSize: Int? = 20,
        sortBy: String? = null, /*relevancy, popularity, publishedAt*/
    ) = api.searchForNews(query, category, page, pageSize, sortBy)

}