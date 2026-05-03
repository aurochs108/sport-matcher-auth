package com.navyblue.sportmatcher.auth.login.email.controller

import com.navyblue.sportmatcher.auth.login.email.dto.EmailLoginRequest
import com.navyblue.sportmatcher.auth.login.email.service.EmailLoginService
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/login/email")
class EmailLoginController(
    private val emailLoginService: EmailLoginService,
) {
    @PostMapping
    fun login(
        @Valid @RequestBody request: EmailLoginRequest,
    ): ResponseEntity<AuthResponse> {
        val response = emailLoginService.login(request)
        return ResponseEntity.ok(response)
    }
}
