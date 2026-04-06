package com.navyblue.sportmatcher.auth.registration.email.controller

import com.navyblue.sportmatcher.auth.config.SecurityConfig
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.registration.email.dto.EmailRegistrationRequest
import com.navyblue.sportmatcher.auth.registration.email.service.EmailAlreadyRegisteredException
import com.navyblue.sportmatcher.auth.registration.email.service.EmailRegistrationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
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
import java.util.UUID

@WebMvcTest(EmailRegistrationController::class)
@Import(SecurityConfig::class, RegistrationEmailExceptionHandler::class)
class EmailRegistrationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var emailRegistrationService: EmailRegistrationService

    @Test
    fun `register returns 201 with tokens on successful registration`() {
        whenever(emailRegistrationService.register(any())).thenReturn(
            AuthResponse(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                expiresIn = 900,
            ),
        )

        val expectedEmail = "test@example.com"
        val expectedPassword = UUID.randomUUID().toString().take(12)
        val expectedDeviceId = UUID.randomUUID().toString()
        mockMvc
            .post("/auth/register/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"$expectedEmail","password":"$expectedPassword","deviceId":"$expectedDeviceId"}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.accessToken") { value("access-token") }
                jsonPath("$.refreshToken") { value("refresh-token") }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.expiresIn") { value(900) }
            }

        verify(emailRegistrationService, times(1)).register(
            EmailRegistrationRequest(
                email = expectedEmail,
                password = expectedPassword,
                deviceId = expectedDeviceId,
            ),
        )
    }

    @Test
    fun `register returns 409 when email is already registered`() {
        whenever(emailRegistrationService.register(any()))
            .thenThrow(EmailAlreadyRegisteredException("Email is already registered"))

        mockMvc
            .post("/auth/register/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@example.com","password":"Password1234","deviceId":"device-001"}"""
            }.andExpect {
                status { isConflict() }
                jsonPath("$.code") { value("EMAIL_ALREADY_REGISTERED") }
            }
    }

    @Test
    fun `register returns 400 when email is invalid`() {
        mockMvc
            .post("/auth/register/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"not-an-email","password":"Password123","deviceId":"device-001"}"""
            }.andExpect {
                status { isBadRequest() }
            }

        verify(emailRegistrationService, never()).register(any())
    }

    @Test
    fun `register returns 400 when password is too short`() {
        val shortPassword = UUID.randomUUID().toString().take(11)
        mockMvc
            .post("/auth/register/email") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"email":"test@example.com","password":"$shortPassword","deviceId":"device-001"}"""
            }.andExpect {
                status { isBadRequest() }
            }

        verify(emailRegistrationService, never()).register(any())
    }
}
