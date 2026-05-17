package com.navyblue.sportmatcher.auth.logout.controller

import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import com.navyblue.sportmatcher.auth.token.repository.RefreshTokenRepository
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LogoutControllerIT(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository,
    @Autowired private val userRepository: UserRepository,
) {
    @Test
    fun `logout revokes refresh token in database`() {
        // given
        val rawToken = UUID.randomUUID().toString()
        val refreshToken = saveRefreshToken(rawToken)

        // when
        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"$rawToken"}"""
            }.andExpect {
                status { isNoContent() }
            }

        // then
        val revokedToken = refreshTokenRepository.findById(refreshToken.id).orElseThrow()
        assertThat(revokedToken.isRevoked).isTrue()
    }

    @Test
    fun `logout returns 204 and does not revoke another token when refresh token does not exist`() {
        // given
        val refreshToken = saveRefreshToken(UUID.randomUUID().toString())

        // when
        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"missing-refresh-token"}"""
            }.andExpect {
                status { isNoContent() }
            }

        // then
        val existingToken = refreshTokenRepository.findById(refreshToken.id).orElseThrow()
        assertThat(existingToken.isRevoked).isFalse()
    }

    private fun saveRefreshToken(rawToken: String): RefreshToken {
        val user =
            userRepository.save(
                User(email = "${rawToken.replace("-", ".")}@example.com"),
            )

        return refreshTokenRepository.save(
            RefreshToken(
                tokenHash = hash(rawToken),
                user = user,
                deviceId = "device-001",
                expiresAt = Instant.now().plusSeconds(3600),
            ),
        )
    }

    private fun hash(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        @Container
        private val postgres =
            PostgreSQLContainer(
                DockerImageName.parse("postgres:16-alpine"),
            )

        @JvmStatic
        @DynamicPropertySource
        fun registerDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
