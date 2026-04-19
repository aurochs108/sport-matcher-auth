package com.navyblue.sportmatcher.auth.registration.email.controller

import com.navyblue.sportmatcher.auth.registration.email.service.EmailAlreadyRegisteredException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException

class RegistrationEmailExceptionHandlerTest {
    private val handler = RegistrationEmailExceptionHandler()

    @Test
    fun `handleEmailAlreadyRegistered returns 409 with correct error code`() {
        val exception = EmailAlreadyRegisteredException("Email is already registered")

        val response = handler.handleEmailAlreadyRegistered(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body?.code).isEqualTo("EMAIL_ALREADY_REGISTERED")
    }

    @Test
    fun `handleValidation returns 400 with validation error code`() {
        val exception: MethodArgumentNotValidException = mock()

        val response = handler.handleValidation(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.code).isEqualTo("VALIDATION_ERROR")
    }
}
