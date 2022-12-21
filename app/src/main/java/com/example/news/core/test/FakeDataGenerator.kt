package com.example.news.core.test

import com.example.news.core.common.NetworkErrorBody
import com.example.news.core.data.local.entities.EntitySavedArticle
import com.example.news.core.data.model.toEntityArticle
import com.example.news.core.data.network.models.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeDataGenerator {

    inline fun <reified T> Gson.fromJson(json: String): T = fromJson<T>(json, T::class.java)
    inline fun <reified T> Gson.toJson(data: T): String = toJson(data)

    val test = Gson().toJson(generateNetworkErrorBody(false))

    fun <T> generateResponse(
        isSuccess: Boolean = true, isBodyEmpty: Boolean = false, successResponse: () -> Response<T>,
    ): Response<T> {
        return if (!isSuccess) {
            Response.error(401,
                Gson().toJson(generateNetworkErrorBody(false))
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        } else {
            if (!isBodyEmpty) {
                successResponse()
            } else {
                Response.success(null)
            }
        }
    }

    fun generateNetworkErrorBody(
        isSuccess: Boolean,
        code: String = "apiKeyInvalid",
        message: String = "Your API key is invalid or incorrect. Check your key, or go to https://newsapi.org to create a free API key.",
    ) = NetworkErrorBody(status = if (isSuccess) "ok" else "error", code = code, message = message)

    fun generateNetworkArticle(identifier: Int) = NetworkArticle(
        author = null,
        content = null,
        description = null,
        publishedAt = "2022-12-21T04:15:35Z",
        source = Source(id = "cnn", name = "CNN"),
        title = "Some Fabulous News $identifier",
        url = "cnn.com/SomeFabulousNews/$identifier",
        urlToImage = null)

    fun generateArticle(identifier: Int) = generateNetworkArticle(identifier).toArticle()

    fun generateEntityArticle(identifier: Int) = generateArticle(identifier).toEntityArticle()

    fun generateNetworkNewsResponse(
        isSuccess: Boolean,
        articleSize: Int = 40,
    ): NetworkNewsResponse {
        val networkArticleList = arrayListOf<NetworkArticle>()
        for (i in 0..articleSize) {
            networkArticleList.add(generateNetworkArticle(i))
        }

        return NetworkNewsResponse(networkArticles = networkArticleList,
            status = if (isSuccess) "ok" else "error",
            totalResults = articleSize)
    }

    fun generateNews(
        isSuccess: Boolean,
        articleSize: Int = 40,
    ) = generateNetworkNewsResponse(isSuccess, articleSize).toNews()

    fun generateEntitySavedArticle(identifier: Int) = EntitySavedArticle(
        content = null,
        description = null,
        publishedAt = "2022-12-21T04:15:35Z",
        source = Source(id = "cnn", name = "CNN"),
        title = "Some Fabulous News $identifier",
        url = "cnn.com/SomeFabulousNews/$identifier",
        urlToImage = null,
        id = identifier,
        author = null
    )

}