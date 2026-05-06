package com.navyblue.sportmatcher.auth.login.email.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.login.email.dto.EmailLoginRequest
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.token.service.JwtService
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.AuthProvider
import com.navyblue.sportmatcher.auth.user.repository.UserCredentialRepository
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import com.navyblue.sportmatcher.auth.utility.CredentialsLogUtility
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailLoginService(
    private val userRepository: UserRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val jwtProperties: JwtProperties,
) {
    private val logger = LoggerFactory.getLogger(EmailLoginService::class.java)

    @Transactional
    fun login(request: EmailLoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email) ?: throw invalidCredentials()
        val credential =
            userCredentialRepository.findByUserAndProvider(user, AuthProvider.EMAIL)
                ?: throw invalidCredentials()
        val passwordHash = credential.passwordHash ?: throw invalidCredentials()

        if (!passwordEncoder.matches(request.password, passwordHash)) {
            throw invalidCredentials()
        }

        logger.debug("User logged in with email: {}", CredentialsLogUtility.maskEmail(user.email))

        val accessToken = jwtService.generateAccessToken(user.id, user.email)
        val refreshToken = refreshTokenService.generateRefreshToken(user, request.deviceId)
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtProperties.accessTokenExpiration,
        )
    }

    private fun invalidCredentials(): InvalidLoginCredentialsException = InvalidLoginCredentialsException("Invalid email or password")
}
