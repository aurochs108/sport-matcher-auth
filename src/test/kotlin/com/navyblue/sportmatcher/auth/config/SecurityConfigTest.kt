package com.navyblue.sportmatcher.auth.config

import com.navyblue.sportmatcher.auth.registration.email.controller.EmailRegistrationController
import com.navyblue.sportmatcher.auth.registration.email.service.EmailRegistrationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(EmailRegistrationController::class)
@Import(SecurityConfig::class)
class SecurityConfigTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var emailRegistrationService: EmailRegistrationService

    @Test
    fun `register endpoint is publicly accessible`() {
        mockMvc.post("/auth/register/email") {
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