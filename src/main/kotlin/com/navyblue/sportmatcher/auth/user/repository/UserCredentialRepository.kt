package com.navyblue.sportmatcher.auth.user.repository

import com.navyblue.sportmatcher.auth.user.entity.AuthProvider
import com.navyblue.sportmatcher.auth.user.entity.User
import com.navyblue.sportmatcher.auth.user.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserCredentialRepository : JpaRepository<UserCredential, UUID> {
    fun findByUserAndProvider(user: User, provider: AuthProvider): UserCredential?
}