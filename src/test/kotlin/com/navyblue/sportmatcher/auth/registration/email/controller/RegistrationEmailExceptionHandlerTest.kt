package com.navyblue.sportmatcher.auth.registration.email.controller

import com.navyblue.sportmatcher.auth.registration.email.service.EmailAlreadyRegisteredException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class RegistrationEmailExceptionHandlerTest {
    private val handler = RegistrationEmailExceptionHandler()

    @Test
    fun `handleEmailAlreadyRegistered returns 409 with correct error code and message`() {
        val exception = EmailAlreadyRegisteredException("Email is already registered")

        val response = handler.handleEmailAlreadyRegistered(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body?.code).isEqualTo("EMAIL_ALREADY_REGISTERED")
        assertThat(response.body?.message).isEqualTo("Email is already registered")
    }

    @Test
    fun `handleValidation returns 400 with joined field errors`() {
        val emailError = FieldError("request", "email", "must be a valid email")
        val passwordError = FieldError("request", "password", "size must be between 12 and 64")
        val bindingResult: BindingResult = mock()
        whenever(bindingResult.fieldErrors).thenReturn(listOf(emailError, passwordError))
        val exception: MethodArgumentNotValidException = mock()
        whenever(exception.bindingResult).thenReturn(bindingResult)

        val response = handler.handleValidation(exception)

        val expectedMessage = "${emailError.field}: ${emailError.defaultMessage}, ${passwordError.field}: ${passwordError.defaultMessage}"
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.code).isEqualTo("VALIDATION_ERROR")
        assertThat(response.body?.message).isEqualTo(expectedMessage)
    }
}
