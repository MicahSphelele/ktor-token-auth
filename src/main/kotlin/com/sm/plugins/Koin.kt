package com.sm.plugins

import com.sm.di.configModule
import com.sm.di.serviceModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(configModule, serviceModule)
    }
}