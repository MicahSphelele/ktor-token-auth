package com.sm.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.token.TokenClaim
import com.sm.domain.models.token.TokenConfig
import java.util.Date

class JwtTokenService: TokenService {

    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {

        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withSubject(config.subject)
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))

        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }

        return token.sign(Algorithm.HMAC256(config.secret))
    }
}