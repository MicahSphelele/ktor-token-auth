package com.sm.plugins.routes

import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.request.RefreshRequest
import com.sm.domain.models.request.SignInRequest
import com.sm.domain.models.request.SignUpRequest
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.token.TokenConfig
import com.sm.extensions.refreshToken
import com.sm.extensions.signin
import com.sm.extensions.signup
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.authRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    post(path = "/v1/user/signup") {

        try {

            val request = call.receiveNullable<SignUpRequest>()

            request?.let {

                signup(request = it, hashingService = hashingService)

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
                    tokenConfig = tokenConfig
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

    post("/v1/user/refresh-token") {
        try {

            val request = call.receiveNullable<RefreshRequest>()

            request?.let {
                refreshToken(
                    request = it,
                    tokenService = tokenService,
                    tokenConfig = tokenConfig
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
}