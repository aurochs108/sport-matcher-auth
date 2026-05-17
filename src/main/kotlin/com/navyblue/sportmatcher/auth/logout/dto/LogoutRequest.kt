package com.navyblue.sportmatcher.auth.logout.dto

import jakarta.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)
