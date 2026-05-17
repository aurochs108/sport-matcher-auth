package com.navyblue.sportmatcher.auth.logout.controller

import com.navyblue.sportmatcher.auth.logout.dto.LogoutRequest
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/logout")
class LogoutController(
    private val refreshTokenService: RefreshTokenService,
) {
    @PostMapping
    fun logout(
        @Valid @RequestBody request: LogoutRequest,
    ): ResponseEntity<Void> {
        refreshTokenService.revokeRefreshToken(request.refreshToken)

        return ResponseEntity.noContent().build()
    }
}
