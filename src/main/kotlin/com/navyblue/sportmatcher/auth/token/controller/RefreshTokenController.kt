package com.navyblue.sportmatcher.auth.token.controller

import com.navyblue.sportmatcher.auth.dto.ErrorResponse
import com.navyblue.sportmatcher.auth.token.dto.RefreshAccessTokenRequest
import com.navyblue.sportmatcher.auth.token.service.InvalidRefreshTokenException
import com.navyblue.sportmatcher.auth.token.service.RefreshAccessTokenService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/refresh")
class RefreshTokenController(
    private val refreshAccessTokenService: RefreshAccessTokenService,
) {
    @PostMapping
    fun refreshAccessToken(
        @Valid @RequestBody request: RefreshAccessTokenRequest,
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(refreshAccessTokenService.refreshAccessToken(request.refreshToken))
        } catch (_: InvalidRefreshTokenException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse(code = "INVALID_REFRESH_TOKEN"),
            )
        }
}
