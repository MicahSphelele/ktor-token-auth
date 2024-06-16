package com.sm.plugins

import com.sm.domain.models.response.APIResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.response.respond

fun Application.configureStatusPage() {
    install(StatusPages) {
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                status,
                message = APIResponse<String>(
                    code = -1,
                    developerMessage = "${call.request.httpMethod.value} method call not allowed for this endpoint",
                    message = "Server error occurred, we are working on it",
                    success = false,
                    response = null
                )
            )
        }
    }
}
