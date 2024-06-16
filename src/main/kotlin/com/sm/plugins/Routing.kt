package com.sm.plugins

import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.domain.models.token.TokenConfig
import com.sm.plugins.routes.authRouting
import com.sm.plugins.routes.protectedRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {

    routing {

        val hashingService by inject<HashingService>()
        val tokenService by inject<TokenService>()

        authRouting(
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )

        protectedRouting()
    }
}
