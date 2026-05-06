package com.navyblue.sportmatcher.auth.login.email.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import com.navyblue.sportmatcher.auth.login.email.dto.EmailLoginRequest
import com.navyblue.sportmatcher.auth.token.service.JwtService
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import com.navyblue.sportmatcher.auth.user.entity.AuthProvider
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.entity.UserCredential
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

class EmailLoginServiceTest {
    private val userRepository: UserRepository = mock()
    private val userCredentialRepository: UserCredentialRepository = mock()
    private val jwtService: JwtService = mock()
    private val refreshTokenService: RefreshTokenService = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtProperties = JwtProperties(secret = "test-secret")

    private val service =
        EmailLoginService(
            userRepository,
            userCredentialRepository,
            jwtService,
            refreshTokenService,
            passwordEncoder,
            jwtProperties,
        )

    private val request =
        EmailLoginRequest(
            email = "test@example.com",
            password = "Password1234",
            deviceId = "device-001",
        )

    private val user = User(email = request.email)

    @Test
    fun `login returns tokens when credentials are valid`() {
        // given
        val credential = UserCredential(user = user, provider = AuthProvider.EMAIL, passwordHash = "hashed")
        whenever(userRepository.findByEmail(request.email)).thenReturn(user)
        whenever(userCredentialRepository.findByUserAndProvider(user, AuthProvider.EMAIL)).thenReturn(credential)
        whenever(passwordEncoder.matches(request.password, "hashed")).thenReturn(true)
        whenever(jwtService.generateAccessToken(user.id, user.email)).thenReturn("access-token")
        whenever(refreshTokenService.generateRefreshToken(user, request.deviceId)).thenReturn("refresh-token")

        // when
        val response = service.login(request)

        // then
        assertThat(response.accessToken).isEqualTo("access-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
        assertThat(response.expiresIn).isEqualTo(jwtProperties.accessTokenExpiration)
        verify(userRepository).findByEmail(request.email)
        verify(userCredentialRepository).findByUserAndProvider(user, AuthProvider.EMAIL)
        verify(passwordEncoder).matches(request.password, "hashed")
        verify(jwtService).generateAccessToken(user.id, user.email)
        verify(refreshTokenService).generateRefreshToken(user, request.deviceId)
    }

    @Test
    fun `login throws InvalidLoginCredentialsException when user does not exist`() {
        // given
        whenever(userRepository.findByEmail(request.email)).thenReturn(null)

        // when
        assertThrows<InvalidLoginCredentialsException> { service.login(request) }

        // then
        verify(userRepository).findByEmail(request.email)
        verify(userCredentialRepository, never()).findByUserAndProvider(any(), any())
        verify(passwordEncoder, never()).matches(any(), any())
        verify(jwtService, never()).generateAccessToken(any(), any())
        verify(refreshTokenService, never()).generateRefreshToken(any(), any())
    }

    @Test
    fun `login throws InvalidLoginCredentialsException when email credential does not exist`() {
        // given
        whenever(userRepository.findByEmail(request.email)).thenReturn(user)
        whenever(userCredentialRepository.findByUserAndProvider(user, AuthProvider.EMAIL)).thenReturn(null)

        // when
        assertThrows<InvalidLoginCredentialsException> { service.login(request) }

        // then
        verify(userRepository).findByEmail(request.email)
        verify(userCredentialRepository).findByUserAndProvider(user, AuthProvider.EMAIL)
        verify(passwordEncoder, never()).matches(any(), any())
        verify(jwtService, never()).generateAccessToken(any(), any())
        verify(refreshTokenService, never()).generateRefreshToken(any(), any())
    }

    @Test
    fun `login throws InvalidLoginCredentialsException when password hash is missing`() {
        // given
        val credential = UserCredential(user = user, provider = AuthProvider.EMAIL, passwordHash = null)
        whenever(userRepository.findByEmail(request.email)).thenReturn(user)
        whenever(userCredentialRepository.findByUserAndProvider(user, AuthProvider.EMAIL)).thenReturn(credential)

        // when
        assertThrows<InvalidLoginCredentialsException> { service.login(request) }

        // then
        verify(userRepository).findByEmail(request.email)
        verify(userCredentialRepository).findByUserAndProvider(user, AuthProvider.EMAIL)
        verify(passwordEncoder, never()).matches(any(), any())
        verify(jwtService, never()).generateAccessToken(any(), any())
        verify(refreshTokenService, never()).generateRefreshToken(any(), any())
    }

    @Test
    fun `login throws InvalidLoginCredentialsException when password does not match`() {
        // given
        val credential = UserCredential(user = user, provider = AuthProvider.EMAIL, passwordHash = "hashed")
        whenever(userRepository.findByEmail(request.email)).thenReturn(user)
        whenever(userCredentialRepository.findByUserAndProvider(user, AuthProvider.EMAIL)).thenReturn(credential)
        whenever(passwordEncoder.matches(request.password, "hashed")).thenReturn(false)

        // when
        assertThrows<InvalidLoginCredentialsException> { service.login(request) }

        // then
        verify(userRepository).findByEmail(request.email)
        verify(userCredentialRepository).findByUserAndProvider(user, AuthProvider.EMAIL)
        verify(passwordEncoder).matches(request.password, "hashed")
        verify(jwtService, never()).generateAccessToken(any(), any())
        verify(refreshTokenService, never()).generateRefreshToken(any(), any())
    }
}
