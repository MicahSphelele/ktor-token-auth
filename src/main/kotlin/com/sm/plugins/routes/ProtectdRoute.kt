package com.sm.plugins.routes

import com.sm.domain.enums.AuthType
import com.sm.domain.models.response.APIResponse
import com.sm.extensions.userEmail
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.protectedRouting() {

    authenticate(AuthType.JWT_AUTH.type) {
        get("/v1/user") {

            val principal = call.principal<JWTPrincipal>()

            call.respond(
                HttpStatusCode.OK,
                message = APIResponse<String>(
                    code = HttpStatusCode.OK.value,
                    message = "Hello ${principal?.userEmail} you have access to this route with a valid token",
                    success = true,
                    response = null
                )
            )
        }
    }
}