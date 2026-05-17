package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.security.MessageDigest

class RefreshTokenServiceTest {
    private val refreshTokenRepository: RefreshTokenRepository = org.mockito.kotlin.mock()
    private val jwtProperties = JwtProperties(secret = "test-secret")
    private val service = RefreshTokenService(refreshTokenRepository, jwtProperties)

    @Test
    fun `revokeRefreshToken returns updated row count`() {
        // given
        val rawToken = "refresh-token"
        whenever(refreshTokenRepository.revokeByTokenHash(hash(rawToken))).thenReturn(1)

        // when
        val updatedRows = service.revokeRefreshToken(rawToken)

        // then
        assertThat(updatedRows).isEqualTo(1)
        verify(refreshTokenRepository).revokeByTokenHash(hash(rawToken))
    }

    @Test
    fun `revokeRefreshToken returns zero when token does not exist`() {
        // given
        val rawToken = "missing-refresh-token"
        whenever(refreshTokenRepository.revokeByTokenHash(hash(rawToken))).thenReturn(0)

        // when
        val updatedRows = service.revokeRefreshToken(rawToken)

        // then
        assertThat(updatedRows).isZero()
        verify(refreshTokenRepository).revokeByTokenHash(hash(rawToken))
    }

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
