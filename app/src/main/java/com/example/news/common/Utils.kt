package com.example.news.common

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

suspend fun <T> retry(
    numberOfRetries: Int,
    delayBetweenRetries: Long = 100,
    block: suspend () -> T
): T {
    repeat(numberOfRetries) {
        try {
            return block()
        } catch (exception: Exception) {
            Log.d("retry_error", "$exception")
        }
        delay(delayBetweenRetries)
    }
    return block()
}

private suspend fun <T> retryWithTimeout(
    numberOfRetries: Int,
    timeout: Long,
    block: suspend () -> T
) = retry(numberOfRetries) {
    withTimeout(timeout) {
        block()
    }
}

fun Activity.makeToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.makeToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}