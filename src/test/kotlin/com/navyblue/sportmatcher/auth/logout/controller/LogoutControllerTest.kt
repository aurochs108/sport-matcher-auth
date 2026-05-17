package com.navyblue.sportmatcher.auth.logout.controller

import com.navyblue.sportmatcher.auth.config.SecurityConfig
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(LogoutController::class)
@Import(SecurityConfig::class)
class LogoutControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var refreshTokenService: RefreshTokenService

    @Test
    fun `logout returns 204 and revokes refresh token`() {
        val refreshToken = UUID.randomUUID().toString()

        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":"$refreshToken"}"""
            }.andExpect {
                status { isNoContent() }
            }

        verify(refreshTokenService).revokeRefreshToken(refreshToken)
    }

    @Test
    fun `logout returns 400 when refresh token is blank`() {
        mockMvc
            .post("/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"refreshToken":""}"""
            }.andExpect {
                status { isBadRequest() }
            }

        verify(refreshTokenService, never()).revokeRefreshToken("")
    }
}
