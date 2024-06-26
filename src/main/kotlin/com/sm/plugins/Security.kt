package com.sm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sm.domain.enums.AuthType
import com.sm.domain.enums.Environment
import com.sm.domain.models.token.TokenConfig
import com.sm.domain.models.response.APIResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity(tokenConfig: TokenConfig) {
    authentication {
        jwt(AuthType.JWT_AUTH_ACCESS_TOKEN.type) {
            realm = Environment.JWT_REALM.value
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )

            validate { credential ->

                if (credential.payload.audience.contains(tokenConfig.audience))
                    JWTPrincipal(credential.payload)
                else
                    null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized, message = APIResponse<String>(
                        HttpStatusCode.Unauthorized.value,
                        message = "Access token invalid or has expired",
                        success = false,
                        response = null
                    )
                )
            }
        }

        jwt(AuthType.JWT_AUTH_REFRESH_TOKEN.type) {
            realm = Environment.JWT_REALM.value
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )

            validate { credential ->

                if (credential.payload.audience.contains(tokenConfig.audience))
                    JWTPrincipal(credential.payload)
                else
                    null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized, message = APIResponse<String>(
                        HttpStatusCode.Unauthorized.value,
                        message = "Refresh token invalid or has expired",
                        success = false,
                        response = null
                    )
                )
            }
        }
    }
}
