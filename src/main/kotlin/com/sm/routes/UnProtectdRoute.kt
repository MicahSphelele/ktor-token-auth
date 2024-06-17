package com.sm.routes

import com.sm.domain.models.response.APIResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.unprotectedRouting() {


    get(path = "/") {

        call.respond(
            HttpStatusCode.OK,
            message = APIResponse<String>(
                code = HttpStatusCode.OK.value,
                message = "Hey you have access to this unprotected route",
                success = true,
                response = null
            )
        )
    }

}