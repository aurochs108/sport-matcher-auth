package com.navyblue.sportmatcher.auth.token.controller

import com.navyblue.sportmatcher.auth.config.SecurityConfig
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.token.service.InvalidRefreshTokenException
import com.navyblue.sportmatcher.auth.token.service.RefreshAccessTokenService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(RefreshTokenController::class)
@Import(SecurityConfig::class)
class RefreshTokenControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var refreshAccessTokenService: RefreshAccessTokenService

    @Test
    fun `refreshAccessToken returns HTTP 200 with tokens on successful refresh`() {
        val refreshToken = UUID.randomUUID().toString()
        whenever(refreshAccessTokenService.refreshAccessToken(refreshToken)).thenReturn(
            AuthResponse(
                accessToken = "new-access-token",
                refreshToken = refreshToken,
                expiresIn = 900,
            ),
        )

        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"$refreshToken"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { value("new-access-token") }
                jsonPath("$.refreshToken") { value(refreshToken) }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.expiresIn") { value(900) }
            }

        verify(refreshAccessTokenService).refreshAccessToken(refreshToken)
    }

    @Test
    fun `refreshAccessToken returns HTTP 401 without body when refresh token is invalid`() {
        val refreshToken = UUID.randomUUID().toString()
        whenever(refreshAccessTokenService.refreshAccessToken(refreshToken))
            .thenThrow(InvalidRefreshTokenException("Invalid refresh token"))

        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"$refreshToken"}"""
            }.andExpect {
                status { isUnauthorized() }
                content { string("") }
            }

        verify(refreshAccessTokenService).refreshAccessToken(refreshToken)
    }

    @Test
    fun `refreshAccessToken returns HTTP 400 when refresh token is blank`() {
        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":""}"""
            }.andExpect {
                status { isBadRequest() }
            }

        verify(refreshAccessTokenService, never()).refreshAccessToken("")
    }
}
