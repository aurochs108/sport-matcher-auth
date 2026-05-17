package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.security.MessageDigest
import java.util.UUID

class RefreshTokenServiceTest {
    private val refreshTokenRepository: RefreshTokenRepository = org.mockito.kotlin.mock()
    private val jwtProperties = JwtProperties(secret = UUID.randomUUID().toString())
    private val service = RefreshTokenService(refreshTokenRepository, jwtProperties)

    @Test
    fun `revokeRefreshToken revokes token by hash`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        whenever(refreshTokenRepository.revokeByTokenHash(rawToken)).thenReturn(1)

        // when
        service.revokeRefreshToken(rawToken)

        // then
        verify(refreshTokenRepository).revokeByTokenHash(hash(rawToken))
    }

    @Test
    fun `revokeRefreshToken revokes not existing token by hash`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        whenever(refreshTokenRepository.revokeByTokenHash(rawToken)).thenReturn(0)

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
