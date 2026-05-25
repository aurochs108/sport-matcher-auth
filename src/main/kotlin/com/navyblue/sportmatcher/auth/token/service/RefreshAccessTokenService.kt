package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant

@Service
class RefreshAccessTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
) {
    @Transactional(readOnly = true)
    fun refreshAccessToken(rawToken: String): AuthResponse {
        val refreshToken =
            refreshTokenRepository.findByTokenHash(hash(rawToken))
                ?: throw invalidRefreshToken()

        if (refreshToken.isRevoked || !refreshToken.expiresAt.isAfter(Instant.now())) {
            throw invalidRefreshToken()
        }

        val user = refreshToken.user
        return AuthResponse(
            accessToken = jwtService.generateAccessToken(user.id, user.email),
            refreshToken = rawToken,
            expiresIn = jwtProperties.accessTokenExpiration,
        )
    }

    private fun invalidRefreshToken(): InvalidRefreshTokenException = InvalidRefreshTokenException("Invalid refresh token")

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
