package com.navyblue.sportmatcher.auth.token.service

import com.navyblue.sportmatcher.auth.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey
import java.time.Instant

@Service
class JwtService(private val jwtProperties: JwtProperties) {

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))
    }

    fun generateAccessToken(userId: UUID, email: String): String {
        val now = Instant.now()
        return Jwts.builder()
            .id(UUID.randomUUID().toString())  // jti — for future Redis blacklist on logout
            .subject(userId.toString())
            .claim("email", email)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(jwtProperties.accessTokenExpiration)))
            .signWith(secretKey)
            .compact()
    }
}