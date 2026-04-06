package com.navyblue.sportmatcher.auth.registration.email.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.registration.email.dto.EmailRegistrationRequest
import com.navyblue.sportmatcher.auth.registration.email.service.EmailAlreadyRegisteredException
import com.navyblue.sportmatcher.auth.token.service.JwtService
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.repository.UserCredentialRepository
import com.navyblue.sportmatcher.auth.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

class EmailRegistrationServiceTest {
    private val userRepository: UserRepository = mock()
    private val userCredentialRepository: UserCredentialRepository = mock()
    private val refreshTokenService: RefreshTokenService = mock()
    private val jwtService: JwtService = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtProperties = JwtProperties(secret = "test-secret")

    private val service =
        EmailRegistrationService(
            userRepository,
            userCredentialRepository,
            refreshTokenService,
            jwtService,
            passwordEncoder,
            jwtProperties,
        )

    private val request =
        EmailRegistrationRequest(
            email = "test@example.com",
            password = "Password123",
            deviceId = "device-001",
        )

    @Test
    fun `register saves user and credential on successful registration`() {
        // given
        val savedUser = User(email = request.email)
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(userRepository.save(any())).thenReturn(savedUser)
        whenever(passwordEncoder.encode(request.password)).thenReturn("hashed")
        whenever(jwtService.generateAccessToken(any(), any())).thenReturn("access-token")
        whenever(refreshTokenService.generateRefreshToken(any(), any())).thenReturn("refresh-token")

        // when
        val response = service.register(request)

        // then
        assertThat(response.accessToken).isEqualTo("access-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
        assertThat(response.expiresIn).isEqualTo(jwtProperties.accessTokenExpiration)
        verify(userRepository).existsByEmail(request.email)
        verify(userRepository).save(any())
        verify(userCredentialRepository).save(any())
        verify(jwtService).generateAccessToken(savedUser.id, savedUser.email)
        verify(refreshTokenService).generateRefreshToken(savedUser, request.deviceId)
    }

    @Test
    fun `register throws EmailAlreadyRegisteredException when email is taken`() {
        // given
        whenever(userRepository.existsByEmail(request.email)).thenReturn(true)

        // when
        assertThrows<EmailAlreadyRegisteredException> { service.register(request) }

        // then
        verify(userRepository).existsByEmail(request.email)
        verify(userRepository, never()).save(any())
        verify(userCredentialRepository, never()).save(any())
        verify(jwtService, never()).generateAccessToken(any(), any())
        verify(refreshTokenService, never()).generateRefreshToken(any(), any())
    }
}
