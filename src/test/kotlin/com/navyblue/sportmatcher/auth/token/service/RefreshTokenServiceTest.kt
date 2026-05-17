package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import java.security.MessageDigest

class RefreshTokenServiceTest {
    private val refreshTokenRepository: RefreshTokenRepository = org.mockito.kotlin.mock()
    private val jwtProperties = JwtProperties(secret = "test-secret")
    private val service = RefreshTokenService(refreshTokenRepository, jwtProperties)

    @Test
    fun `revokeRefreshToken revokes token by hash`() {
        // given
        val rawToken = "refresh-token"

        // when
        service.revokeRefreshToken(rawToken)

        // then
        verify(refreshTokenRepository).revokeByTokenHash(hash(rawToken))
    }

    @Test
    fun `revokeRefreshToken delegates missing token to repository update`() {
        // given
        val rawToken = "missing-refresh-token"

        // when
        service.revokeRefreshToken(rawToken)

        // then
        verify(refreshTokenRepository).revokeByTokenHash(hash(rawToken))
    }

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
