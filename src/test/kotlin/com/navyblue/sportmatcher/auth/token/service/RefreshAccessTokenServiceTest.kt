package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import com.navyblue.sportmatcher.auth.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

class RefreshAccessTokenServiceTest {
    private val refreshTokenRepository: RefreshTokenRepository = mock()
    private val jwtService: JwtService = mock()
    private val jwtProperties = JwtProperties(secret = UUID.randomUUID().toString())
    private val service = RefreshAccessTokenService(refreshTokenRepository, jwtService, jwtProperties)

    @Test
    fun `refreshAccessToken returns new access token for valid refresh token`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        val user = User(email = "test@example.com")
        val refreshToken =
            refreshToken(
                rawToken = rawToken,
                user = user,
                expiresAt = Instant.now().plusSeconds(60),
                isRevoked = false,
            )
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(refreshToken)
        whenever(jwtService.generateAccessToken(user.id, user.email)).thenReturn("new-access-token")

        // when
        val response = service.refreshAccessToken(rawToken)

        // then
        assertThat(response.accessToken).isEqualTo("new-access-token")
        assertThat(response.refreshToken).isEqualTo(rawToken)
        assertThat(response.expiresIn).isEqualTo(jwtProperties.accessTokenExpiration)
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
        verify(jwtService).generateAccessToken(user.id, user.email)
    }

    @Test
    fun `refreshAccessToken throws InvalidRefreshTokenException when token does not exist`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(null)

        // when
        assertThrows<InvalidRefreshTokenException> {
            service.refreshAccessToken(rawToken)
        }

        // then
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
        verifyNoInteractions(jwtService)
    }

    @Test
    fun `refreshAccessToken throws InvalidRefreshTokenException when token is revoked`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        val refreshToken =
            refreshToken(
                rawToken = rawToken,
                expiresAt = Instant.now().plusSeconds(60),
                isRevoked = true,
            )
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(refreshToken)

        // when
        assertThrows<InvalidRefreshTokenException> {
            service.refreshAccessToken(rawToken)
        }

        // then
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
        verifyNoInteractions(jwtService)
    }

    @Test
    fun `refreshAccessToken throws InvalidRefreshTokenException when token is expired`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        val refreshToken =
            refreshToken(
                rawToken = rawToken,
                expiresAt = Instant.now().minusSeconds(60),
                isRevoked = false,
            )
        whenever(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(refreshToken)

        // when
        assertThrows<InvalidRefreshTokenException> {
            service.refreshAccessToken(rawToken)
        }

        // then
        verify(refreshTokenRepository).findByTokenHash(hash(rawToken))
        verifyNoInteractions(jwtService)
    }

    private fun refreshToken(
        rawToken: String,
        user: User = User(email = "${UUID.randomUUID()}@example.com"),
        expiresAt: Instant,
        isRevoked: Boolean,
    ): RefreshToken =
        RefreshToken(
            tokenHash = hash(rawToken),
            user = user,
            deviceId = UUID.randomUUID().toString(),
            expiresAt = expiresAt,
            isRevoked = isRevoked,
        )

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
