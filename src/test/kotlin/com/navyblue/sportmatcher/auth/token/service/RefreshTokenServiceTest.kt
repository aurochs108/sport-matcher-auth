package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import com.navyblue.sportmatcher.auth.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.security.MessageDigest
import java.time.Instant

class RefreshTokenServiceTest {
    private val refreshTokenRepository: RefreshTokenRepository = org.mockito.kotlin.mock()
    private val jwtProperties = JwtProperties(secret = "test-secret")
    private val service = RefreshTokenService(refreshTokenRepository, jwtProperties)

    @Test
    fun `revokeRefreshToken marks matching token as revoked`() {
        // given
        val rawToken = "refresh-token"
        val refreshToken =
            RefreshToken(
                tokenHash = hash(rawToken),
                user = User(email = "test@example.com"),
                deviceId = "device-001",
                expiresAt = Instant.now().plusSeconds(3600),
            )
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(refreshToken)

        // when
        service.revokeRefreshToken(rawToken)

        // then
        assertThat(refreshToken.isRevoked).isTrue()
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
    }

    @Test
    fun `revokeRefreshToken does nothing when token does not exist`() {
        // given
        val rawToken = "missing-refresh-token"
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(null)

        // when
        service.revokeRefreshToken(rawToken)

        // then
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
        verify(refreshTokenRepository, never()).save(org.mockito.kotlin.any())
    }

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
