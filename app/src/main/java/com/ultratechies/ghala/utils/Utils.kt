package com.ultratechies.ghala.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ultratechies.ghala.data.repository.APIResource

const val BASE_URL = "http://192.168.100.224:8080/api/"

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

fun View.hideKeyboard() {
    val closeKeyboard =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    closeKeyboard.hideSoftInputFromWindow(this.windowToken, 0)
}

fun EditText.showKeyboard() {
    if (requestFocus()) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        setSelection(text.length)
    }
}
