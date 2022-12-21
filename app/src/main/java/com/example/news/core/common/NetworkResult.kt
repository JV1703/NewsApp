package com.example.news.core.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.HttpURLConnection

sealed class NetworkResource<out T> {
    data class Success<out T>(val data: T) : NetworkResource<T>()
    data class Error(val errMsg: String? = null, val errCode: Int? = null) :
        NetworkResource<Nothing>()

    object Empty : NetworkResource<Nothing>()
}

suspend fun <T, R> safeNetworkCall(
    dispatcher: CoroutineDispatcher,
    networkCall: suspend () -> Response<T>,
    conversion: (T) -> R,
): NetworkResource<R> {
    return withContext(dispatcher) {
        try {
            val response = networkCall()

            when {
                response.message().toString().contains("timeout") -> {
                    NetworkResource.Error(errMsg = "Network Timeout")
                }

                response.errorBody() != null -> {
                    val errorBody = getErrorBody(response)
                    NetworkResource.Error(errMsg = errorBody.toString())
                }

                response.isSuccessful -> {
                    val content = response.body()
                    if (content == null) {
                        NetworkResource.Empty
                    } else {
                        val convertedData = conversion(content)
                        NetworkResource.Success(data = convertedData)
                    }
                }

                else -> {
                    NetworkResource.Error(errMsg = response.message())
                }
            }
        } catch (e: Exception) {
            NetworkResource.Error(errMsg = e.message ?: "Non api error")
        }
    }
}

private fun mapResponseCodeToString(code: Int): String {
    return when (code) {
        HttpURLConnection.HTTP_NOT_FOUND -> "Not Found"
        HttpURLConnection.HTTP_UNAUTHORIZED -> "User Unauthorized"
        else -> "Some Unknown Error Occurred"
    }
}

data class NetworkErrorBody(
    val status: String,
    val code: String,
    val message: String,
)

fun <T> getErrorBody(response: Response<T>): T? {
    return Gson().fromJson(
        response.errorBody()!!.charStream(),
        object : TypeToken<NetworkErrorBody>() {}.type
    )
}