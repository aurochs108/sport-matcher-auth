package com.navyblue.sportmatcher.auth.token.dto

import jakarta.validation.constraints.NotBlank

data class RefreshAccessTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)
