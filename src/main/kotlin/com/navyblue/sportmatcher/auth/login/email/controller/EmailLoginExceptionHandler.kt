package com.navyblue.sportmatcher.auth.login.email.controller

import com.navyblue.sportmatcher.auth.dto.ErrorResponse
import com.navyblue.sportmatcher.auth.login.email.service.InvalidLoginCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class EmailLoginExceptionHandler {
    @ExceptionHandler(InvalidLoginCredentialsException::class)
    fun handleInvalidLoginCredentials(ex: InvalidLoginCredentialsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(code = "INVALID_LOGIN_CREDENTIALS"),
        )
}
