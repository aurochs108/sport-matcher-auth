package com.navyblue.sportmatcher.auth.logout.controller

import com.navyblue.sportmatcher.auth.infrastructure.PostgresContainerSupport
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class LogoutControllerIT(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val refreshTokenService: RefreshTokenService,
    @Autowired private val userRepository: UserRepository,
) : PostgresContainerSupport() {
    @Test
    fun `logout returns 204 and revokes refresh token in database`() {
        // given
        val savedRefreshToken = saveRefreshToken()

        // when
        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"${savedRefreshToken.refreshToken}"}"""
            }.andExpect {
                status { isNoContent() }
            }

        // then
        assertThat(isRefreshTokenRevokedByDeviceId(savedRefreshToken.deviceId)).isTrue()
    }

    @Test
    fun `logout returns 204 and does not revoke another token when refresh token does not exist`() {
        // given
        val savedRefreshToken = saveRefreshToken()

        // when
        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"missing-refresh-token"}"""
            }.andExpect {
                status { isNoContent() }
            }

        // then
        assertThat(isRefreshTokenRevokedByDeviceId(savedRefreshToken.deviceId)).isFalse()
    }

    private fun saveRefreshToken(): SavedRefreshToken {
        val user =
            userRepository.save(
                User(email = "${UUID.randomUUID()}@example.com"),
            )
        val deviceId = UUID.randomUUID().toString()
        val refreshToken = refreshTokenService.generateRefreshToken(user, deviceId)

        return SavedRefreshToken(refreshToken, deviceId)
    }

    private data class SavedRefreshToken(
        val refreshToken: String,
        val deviceId: String,
    )

    private fun isRefreshTokenRevokedByDeviceId(deviceId: String): Boolean {
        val isRevoked =
            jdbcTemplate.queryForObject(
                """
                    select is_revoked from refresh_tokens
                    where device_id = ?
                """,
                Boolean::class.javaObjectType,
                deviceId,
            )

        return isRevoked ?: error("Refresh token for device $deviceId was not found")
    }
}
