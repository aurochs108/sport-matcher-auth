package com.navyblue.sportmatcher.auth.logout.controller

import com.navyblue.sportmatcher.auth.dto.ErrorResponse
import com.navyblue.sportmatcher.auth.logout.dto.LogoutRequest
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
    ): ResponseEntity<*> =
        when (refreshTokenService.revokeRefreshToken(request.refreshToken)) {
            0 ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse(code = "REFRESH_TOKEN_NOT_FOUND"),
                )
            else -> ResponseEntity.noContent().build<Void>()
        }
}
