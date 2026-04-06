package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import com.navyblue.sportmatcher.auth.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) {
    @Transactional
    fun generateRefreshToken(
        user: User,
        deviceId: String,
    ): String {
        val rawToken = UUID.randomUUID().toString()

        refreshTokenRepository.save(
            RefreshToken(
                tokenHash = hash(rawToken),
                user = user,
                deviceId = deviceId,
                expiresAt = Instant.now().plusSeconds(jwtProperties.refreshTokenExpiration),
            ),
        )

        return rawToken
    }

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
