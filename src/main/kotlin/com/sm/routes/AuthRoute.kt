package com.sm.routes

import com.sm.domain.enums.AuthType
import com.sm.domain.interfaces.AuthDatasource
import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.request.SignInRequest
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.token.TokenConfig
import com.sm.extensions.refreshToken
import com.sm.extensions.signin
import com.sm.extensions.signup
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Routing.authRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    val authDatasource by inject<AuthDatasource>()

    post(path = "/v1/user/signup") {

        try {

            val request = call.receiveNullable<SignUpRequest>()

            request?.let {

                signup(request = it, hashingService = hashingService, authDatasource = authDatasource)

            } ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    message = APIResponse<String>(
                        code = HttpStatusCode.BadRequest.value,
                        message = "Request body is required",
                        success = false,
                        response = null
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                message = APIResponse<String>(
                    code = HttpStatusCode.BadRequest.value,
                    message = HttpStatusCode.BadRequest.description,
                    success = false,
                    response = null
                )
            )
        }
    }

    post("/v1/user/signin") {
        try {

            val request = call.receiveNullable<SignInRequest>()

            request?.let {

                signin(
                    request = it,
                    hashingService = hashingService,
                    tokenService = tokenService,
                    tokenConfig = tokenConfig,
                    authDatasource = authDatasource
                )

            } ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    message = APIResponse<String>(
                        code = HttpStatusCode.BadRequest.value,
                        message = "Request body is required",
                        success = false,
                        response = null
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                message = APIResponse<String>(
                    code = HttpStatusCode.BadRequest.value,
                    message = HttpStatusCode.BadRequest.description,
                    success = false,
                    response = null
                )
            )
        }
    }

    authenticate(AuthType.JWT_AUTH_REFRESH_TOKEN.type) {

        post("/v1/user/refresh-token") {

            try {

                val principal = call.principal<JWTPrincipal>()

                principal?.let { jwtPrincipal ->
                    refreshToken(
                        tokenService = tokenService,
                        tokenConfig = tokenConfig,
                        principal = jwtPrincipal
                    )
                } ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        message = APIResponse<String>(
                            code = HttpStatusCode.BadRequest.value,
                            message = "Invalid refresh token",
                            success = false,
                            response = null
                        )
                    )
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    message = APIResponse<String>(
                        code = HttpStatusCode.BadRequest.value,
                        message = HttpStatusCode.BadRequest.description,
                        success = false,
                        response = null
                    )
                )
            }
        }
    }

}