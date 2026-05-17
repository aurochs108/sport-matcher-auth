package com.navyblue.sportmatcher.auth.token.repository

import com.navyblue.sportmatcher.auth.token.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    @Modifying
    @Query(
        """
        update RefreshToken refreshToken
        set refreshToken.isRevoked = true
        where refreshToken.tokenHash = :tokenHash
        """,
    )
    fun revokeByTokenHash(
        @Param("tokenHash") tokenHash: String,
    ): Int
}
