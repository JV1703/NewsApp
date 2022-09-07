package com.example.news.common

import android.util.Log
import retrofit2.Response
import java.net.HttpURLConnection

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String?, data: T? = null) :
        NetworkResult<T>(data, message)

//    class Loading<T> : NetworkResult<T>()

}

fun <T : Any?> networkResultHandler(response: Response<T>): NetworkResult<T> {

    return when {
        response.message().toString().contains("timeout") -> {
            Log.i("network_result", "timeout")
            NetworkResult.Error("Timeout")
        }
        response.errorBody() != null -> {
            NetworkResult.Error(
                "response code: ${response.code()}, message: ${mapResponseCodeToString(response.code())}, errorBody: ${
                    response.errorBody()!!.string()
                }"
            )
        }
        (response.isSuccessful && response.body() != null) -> {
            val response = response.body()
            Log.i("network_result", "isSuccessful")
            NetworkResult.Success(response!!)
        }
        else -> {
            Log.i(
                "network_result",
                "else - error code: ${response.code()}, msg: ${response.message()}"
            )
            NetworkResult.Error("error code: ${response.code()}, msg: $response")
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