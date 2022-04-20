package com.ultratechies.ghala.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ultratechies.ghala.data.repository.APIResource

const val BASE_URL = "https://625ebdf6873d6798e2ad24eb.mockapi.io/api/v1/"

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

private const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


fun validateEmail(email: String): Boolean {
    return email.matches(emailPattern.toRegex())
}

fun View.handleApiError(
    failure: APIResource.Error,
    action: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> snackbar("Network Error", action)
        failure.errorCode == 401 -> {
            snackbar("Unauthorized request", action)
        }
        failure.errorCode == 404 -> {
            snackbar("Resource not found", action)
        }
        failure.errorCode == 422 -> {
            snackbar("Validation error", action)
        }
        failure.errorCode == 500 -> {
            try {
                val errorBody =
                    Gson().fromJson(failure.errorBody?.string(), JsonObject::class.java)
                snackbar(errorBody.get("message").asString, action)
            } catch (e: Exception) {
                snackbar("Internal server error", action)
            }

        }
        failure.errorCode == 503 -> {
            snackbar("Service unavailable", action)
        }
        failure.errorCode == 504 -> {
            snackbar("Gateway timeout", action)
        }
        failure.errorCode == 0 -> {
            snackbar("Unknown error", action)
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            snackbar(error, action)
        }
    }
}
