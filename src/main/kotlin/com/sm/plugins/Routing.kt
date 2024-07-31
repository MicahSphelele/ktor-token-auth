package com.sm.plugins

import com.sm.domain.models.token.TokenConfig
import com.sm.routes.authRouting
import com.sm.routes.protectedRouting
import com.sm.routes.unprotectedRouting
import com.sm.routes.webSocketRouting
import com.sm.routes.webSocketRoutingIO
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(tokenConfig: TokenConfig) {

    routing {
        authRouting(tokenConfig = tokenConfig)
        protectedRouting()
        unprotectedRouting()
        webSocketRouting(application = application)
        webSocketRoutingIO(application = application)
    }
}
