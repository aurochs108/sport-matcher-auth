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
        assertThat(isRefreshTokenRevoked(savedRefreshToken.refreshTokenId)).isTrue()
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
        assertThat(isRefreshTokenRevoked(savedRefreshToken.refreshTokenId)).isFalse()
    }

    private fun saveRefreshToken(): SavedRefreshToken {
        val user =
            userRepository.save(
                User(email = "${UUID.randomUUID()}@example.com"),
            )
        val rawToken = refreshTokenService.generateRefreshToken(user, UUID.randomUUID().toString())
        val refreshTokenId = findRefreshTokenIdByUserId(user.id)

        return SavedRefreshToken(rawToken, refreshTokenId)
    }

    private data class SavedRefreshToken(
        val refreshToken: String,
        val refreshTokenId: UUID,
    )

    @Suppress("ktlint:standard:function-expression-body")
    private fun findRefreshTokenIdByUserId(userId: UUID): UUID {
        return jdbcTemplate
            .queryForObject(
                """
                    select id from refresh_tokens
                    where user_id = ?
                """,
                UUID::class.java,
                userId,
            ) ?: error("Refresh token for user $userId was not found")
    }

    private fun isRefreshTokenRevoked(refreshTokenId: UUID): Boolean {
        val isRevoked =
            jdbcTemplate.queryForObject(
                """
                    select is_revoked from refresh_tokens
                    where id = ?
                """,
                Boolean::class.javaObjectType,
                refreshTokenId,
            )

        return isRevoked ?: error("Refresh token $refreshTokenId was not found")
    }
}
