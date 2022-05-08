package com.ultratechies.ghala.utils

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.NumberFormat
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ultratechies.ghala.data.repository.APIResource
import java.text.SimpleDateFormat

const val BASE_URL = "http://192.168.0.108:8080/api/"


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
@SuppressLint("SimpleDateFormat")
fun formatDate(date: String, originalFormat: String, expectedFormat: String): String {
    return try {
        val originalDateTime = SimpleDateFormat(originalFormat).parse(date)
        SimpleDateFormat(expectedFormat).format(originalDateTime!!)
    } catch (e: Exception) {
        date
    }
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
fun parseErrors(failure: APIResource.Error): String {
    return when {
        failure.isNetworkError -> "Network Error"
        failure.errorCode == 401 -> {
            "Unauthorized request"
        }
        failure.errorCode == 404 -> {
            ("Resource not found")
        }
        failure.errorCode == 422 -> {
            ("Validation error")
        }
        failure.errorCode == 500 -> {
            try {
                val errorBody =
                    Gson().fromJson(failure.errorBody?.string(), JsonObject::class.java)
                (errorBody.get("message").asString)
            } catch (e: Exception) {
                ("Internal server error")
            }
        }
        failure.errorCode == 504 -> {
            ("Gateway timeout")
        }
        failure.errorCode == 0 -> {
            ("Unknown error")
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            (error)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
 fun getFormattedNumbers(amount:Int): String? {
return NumberFormat.getNumberInstance().format(amount)

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

fun View.gone() {
    this.visibility = View.GONE
}