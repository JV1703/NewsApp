//package com.example.news.common
//
//import retrofit2.HttpException
//import retrofit2.Response
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.SocketTimeoutException
//import java.net.UnknownHostException
//
//open class NetworkResultHandler {
//
//    protected suspend fun <T : Any> createCall(call: suspend () -> Response<T>): NetworkResult<T> {
//
//        val response: Response<T>
//        try {
//            response = call.invoke()
//        } catch (t: Throwable) {
//            t.printStackTrace()
//            return NetworkResult.Error(
//                error = mapToNetworkError(t),
//                message = mapToNetworkErrorString(t)
//            )
//        }
//
//        if (response.isSuccessful) {
//            if (response.body() != null) {
//                return NetworkResult.Success(response.body()!!)
//            }
//        } else {
//            val errorBody = response.errorBody()
//            return if (errorBody != null) {
//                NetworkResult.Error(
//                    error = mapApiException(response.code()),
//                    message = mapApiExceptionString(response.code())
//                )
//            } else NetworkResult.Error(
//                error = mapApiException(0),
//                message = mapApiExceptionString(response.code())
//            )
//        }
//        return NetworkResult.Error(
//            error = HttpException(response),
//            message = mapApiExceptionString(response.code())
//        )
//    }
//
//    private fun mapApiException(code: Int): Exception {
//        return when (code) {
//            HttpURLConnection.HTTP_NOT_FOUND -> IOException("Not Found")
//            HttpURLConnection.HTTP_UNAUTHORIZED -> IOException("User Unauthorized")
//            else -> Exception("Some Unknown Error Occurred")
//        }
//    }
//
//    private fun mapToNetworkError(t: Throwable): Exception {
//        return when (t) {
//            is SocketTimeoutException
//            -> SocketTimeoutException("Connection Timed Out")
//            is UnknownHostException
//            -> IOException("No Internet Connection")
//            else
//            -> Exception("Some Unknown Error Occurred")
//        }
//    }
//
//    private fun mapApiExceptionString(code: Int): String {
//        return when (code) {
//            HttpURLConnection.HTTP_NOT_FOUND -> "Not Found"
//            HttpURLConnection.HTTP_UNAUTHORIZED -> "User Unauthorized"
//            else -> "Some Unknown Error Occurred"
//        }
//    }
//
//    private fun mapToNetworkErrorString(t: Throwable): String {
//        return when (t) {
//            is SocketTimeoutException
//            -> "Connection Timed Out"
//            is UnknownHostException
//            -> "No Internet Connection"
//            else
//            -> "Some Unknown Error Occurred"
//        }
//    }
//
//}