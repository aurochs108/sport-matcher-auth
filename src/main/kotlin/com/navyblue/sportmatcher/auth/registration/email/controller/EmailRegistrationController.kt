package com.navyblue.sportmatcher.auth.registration.email.controller

import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.registration.email.dto.EmailRegistrationRequest
import com.navyblue.sportmatcher.auth.registration.email.service.EmailRegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/register/email")
class EmailRegistrationController(
    private val emailRegistrationService: EmailRegistrationService
) {

    @PostMapping
    fun register(@Valid @RequestBody request: EmailRegistrationRequest): ResponseEntity<AuthResponse> {
        val response = emailRegistrationService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}