package com.navyblue.sportmatcher.auth.registration.email.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.registration.dto.AuthResponse
import com.navyblue.sportmatcher.auth.registration.email.dto.EmailRegistrationRequest
import com.navyblue.sportmatcher.auth.token.service.JwtService
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.AuthProvider
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.entity.UserCredential
import com.navyblue.sportmatcher.auth.user.repository.UserCredentialRepository
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailRegistrationService(
    private val userRepository: UserRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val refreshTokenService: RefreshTokenService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProperties: JwtProperties,
) {
    private val logger = LoggerFactory.getLogger(EmailRegistrationService::class.java)
    @Transactional
    fun register(request: EmailRegistrationRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyRegisteredException("Email is already registered")
        }

        val user = userRepository.save(User(email = request.email))
        userCredentialRepository.save(
            UserCredential(
                user = user,
                provider = AuthProvider.EMAIL,
                passwordHash = passwordEncoder.encode(request.password),
            ),
        )

        logger.debug("User registered with email: {}", user.email)

        val accessToken = jwtService.generateAccessToken(user.id, user.email)
        val refreshToken = refreshTokenService.generateRefreshToken(user, request.deviceId)
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtProperties.accessTokenExpiration,
        )
    }
}
