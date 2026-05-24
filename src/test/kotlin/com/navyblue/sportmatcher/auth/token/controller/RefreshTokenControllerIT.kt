package com.navyblue.sportmatcher.auth.token.controller

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.infrastructure.PostgresContainerSupport
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenControllerIT(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val refreshTokenService: RefreshTokenService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val jwtProperties: JwtProperties,
) : PostgresContainerSupport() {
    @Test
    fun `refreshAccessToken returns 200 with new access token for valid refresh token`() {
        val refreshToken = saveRefreshToken()

        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"$refreshToken"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { value(not(blankOrNullString())) }
                jsonPath("$.refreshToken") { value(refreshToken) }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.expiresIn") { value(jwtProperties.accessTokenExpiration) }
            }
    }

    @Test
    fun `refreshAccessToken returns 401 without body for missing refresh token`() {
        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"missing-refresh-token"}"""
            }.andExpect {
                status { isUnauthorized() }
                content { string("") }
            }
    }

    private fun saveRefreshToken(): String {
        val user =
            userRepository.save(
                User(email = "${UUID.randomUUID()}@example.com"),
            )

        return refreshTokenService.generateRefreshToken(
            user = user,
            deviceId = UUID.randomUUID().toString(),
        )
    }
}
