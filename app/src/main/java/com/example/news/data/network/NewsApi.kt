package com.example.news.data.network

import com.example.news.data.network.models.NetworkNewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("top-headlines/")
    suspend fun getNews(
        @Query("country") countryCode: String = "us",
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int? = 20
    ): Response<NetworkNewsResponse>

    @GET("everything/")
    suspend fun searchForNews(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int? = 20,
        @Query("sortBy") sortBy: String? = null /*relevancy, popularity, publishedAt*/
    ): Response<NetworkNewsResponse>

}