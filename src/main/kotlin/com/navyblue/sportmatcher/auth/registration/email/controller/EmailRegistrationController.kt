package com.navyblue.sportmatcher.auth.registration.email.controller

import com.navyblue.sportmatcher.auth.registration.email.dto.EmailRegistrationRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/register/email")
class EmailRegistrationController {

    @PostMapping
    fun register(@RequestBody request: EmailRegistrationRequest): String {
        return "Registering user: ${request.email}, deviceId: ${request.deviceId}"
    }
}