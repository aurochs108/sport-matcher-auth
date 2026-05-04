package com.navyblue.sportmatcher.auth.login.email.controller

import com.navyblue.sportmatcher.auth.config.SecurityConfig
import com.navyblue.sportmatcher.auth.login.email.dto.EmailLoginRequest
import com.navyblue.sportmatcher.auth.login.email.service.EmailLoginService
import com.navyblue.sportmatcher.auth.login.email.service.InvalidLoginCredentialsException
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(EmailLoginController::class)
@Import(SecurityConfig::class)
class EmailLoginControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var emailLoginService: EmailLoginService

    @Test
    fun `login returns 200 with tokens on successful login`() {
        whenever(emailLoginService.login(any())).thenReturn(
            AuthResponse(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                expiresIn = 900,
            ),
        )

        val expectedEmail = "test@example.com"
        val expectedPassword = "Password1234"
        val expectedDeviceId = "device-001"
        mockMvc
            .post("/auth/login/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"$expectedEmail","password":"$expectedPassword","deviceId":"$expectedDeviceId"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { value("access-token") }
                jsonPath("$.refreshToken") { value("refresh-token") }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.expiresIn") { value(900) }
            }

        verify(emailLoginService, times(1)).login(
            EmailLoginRequest(
                email = expectedEmail,
                password = expectedPassword,
                deviceId = expectedDeviceId,
            ),
        )
    }

    @Test
    fun `login returns 401 when credentials are invalid`() {
        whenever(emailLoginService.login(any()))
            .thenThrow(InvalidLoginCredentialsException("Invalid email or password"))

        mockMvc
            .post("/auth/login/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@example.com","password":"Password1234","deviceId":"device-001"}"""
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("INVALID_LOGIN_CREDENTIALS") }
            }
    }
}
