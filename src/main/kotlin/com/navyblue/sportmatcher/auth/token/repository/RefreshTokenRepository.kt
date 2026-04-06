package com.navyblue.sportmatcher.auth.token.repository

import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID>
