package com.navyblue.sportmatcher.auth.login.email.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailLoginRequest(
    @field:Email(message = "Must be a valid email address")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
    @field:NotBlank(message = "Device ID is required")
    val deviceId: String,
)
