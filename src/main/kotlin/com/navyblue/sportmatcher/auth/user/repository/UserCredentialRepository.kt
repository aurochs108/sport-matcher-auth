package com.navyblue.sportmatcher.auth.user.repository

import com.navyblue.sportmatcher.auth.user.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserCredentialRepository : JpaRepository<UserCredential, UUID>
