package com.sm.routes

import com.sm.domain.enums.AuthType
import com.sm.domain.interfaces.AuthDatasource
import com.sm.domain.models.response.APIResponse
import com.sm.domain.models.user.UserDTO
import com.sm.extensions.email
import com.sm.extensions.id
import com.sm.extensions.user.toUserDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Routing.protectedRouting() {

    val authDatasource by inject<AuthDatasource>()

    authenticate(AuthType.JWT_AUTH_ACCESS_TOKEN.type) {

        get(path = "/v1/user") {

            val principal = call.principal<JWTPrincipal>()

            val userDocument = authDatasource.getById(id = principal?.id ?: "")

            userDocument?.let { user ->
                call.respond(
                    HttpStatusCode.OK,
                    message = APIResponse(
                        code = HttpStatusCode.OK.value,
                        message = "Hey ${user.firstName} you have access to this protected route",
                        success = true,
                        response = user.toUserDTO
                    )
                )
            } ?: kotlin.run {
                call.respond(
                    HttpStatusCode.NotFound,
                    message = APIResponse<String>(
                        code = HttpStatusCode.NotFound.value,
                        message = "User not found",
                        success = false,
                        response = null
                    )
                )
            }
        }
    }
}