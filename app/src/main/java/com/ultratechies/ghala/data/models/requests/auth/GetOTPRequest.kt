package com.ultratechies.ghala.data.models.requests.auth

data class GetOTPRequest(
    var email: String? = null,
    var name: String ? = null,
    var phoneNumber: String? = null
)