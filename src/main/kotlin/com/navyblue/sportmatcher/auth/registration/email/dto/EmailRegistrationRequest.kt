package com.navyblue.sportmatcher.auth.registration.email.dto

data class EmailRegistrationRequest(
    val email: String,
    val password: String,
    val deviceId: String
)