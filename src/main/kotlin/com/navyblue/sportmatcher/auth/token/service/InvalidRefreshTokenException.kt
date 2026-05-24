package com.navyblue.sportmatcher.auth.token.service

class InvalidRefreshTokenException(
    message: String,
) : RuntimeException(message)
