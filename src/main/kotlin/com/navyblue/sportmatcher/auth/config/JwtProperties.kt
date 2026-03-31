package com.navyblue.sportmatcher.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiration: Long = 900,
    val refreshTokenExpiration: Long = 604800
)