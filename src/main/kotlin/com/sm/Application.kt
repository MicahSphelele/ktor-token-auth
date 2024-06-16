package com.sm

import com.sm.domain.models.token.TokenConfig
import com.sm.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    //Koin plugin configuration should always be initialized above other plugins
    configureKoin()
    configureStatusPage()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    val tokenConfig by inject<TokenConfig>()
    configureSecurity(tokenConfig = tokenConfig)
    configureRouting(tokenConfig = tokenConfig)
}
