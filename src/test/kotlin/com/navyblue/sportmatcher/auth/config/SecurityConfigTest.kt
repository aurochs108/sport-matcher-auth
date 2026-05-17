package com.navyblue.sportmatcher.auth.config

import com.navyblue.sportmatcher.auth.login.email.controller.EmailLoginController
import com.navyblue.sportmatcher.auth.login.email.service.EmailLoginService
import com.navyblue.sportmatcher.auth.logout.controller.LogoutController
import com.navyblue.sportmatcher.auth.registration.email.controller.EmailRegistrationController
import com.navyblue.sportmatcher.auth.registration.email.service.EmailRegistrationService
import com.navyblue.sportmatcher.auth.token.service.RefreshTokenService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [
        EmailRegistrationController::class,
        EmailLoginController::class,
        LogoutController::class,
    ],
)
@Import(SecurityConfig::class)
class SecurityConfigTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockitoBean
    lateinit var emailRegistrationService: EmailRegistrationService

    @MockitoBean
    lateinit var emailLoginService: EmailLoginService

    @MockitoBean
    lateinit var refreshTokenService: RefreshTokenService

    @Test
    fun `register endpoint is publicly accessible`() {
        mockMvc
            .post("/auth/register/email") {
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `login endpoint is publicly accessible`() {
        mockMvc
            .post("/auth/login/email") {
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `logout endpoint is publicly accessible`() {
        mockMvc
            .post("/auth/logout") {
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `protected endpoint returns 403 when not authenticated`() {
        mockMvc.get("/random/protected/endpoint").andExpect {
            status { isForbidden() }
        }
    }

    @Test
    @WithMockUser
    fun `protected endpoint returns 404 when authenticated but endpoint does not exist`() {
        mockMvc.get("/random/protected/endpoint").andExpect {
            status { isNotFound() }
        }
    }
}
