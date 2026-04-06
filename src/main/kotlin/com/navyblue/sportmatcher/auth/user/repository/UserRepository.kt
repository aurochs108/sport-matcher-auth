package com.navyblue.sportmatcher.auth.user.repository

import com.navyblue.sportmatcher.auth.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean
}
