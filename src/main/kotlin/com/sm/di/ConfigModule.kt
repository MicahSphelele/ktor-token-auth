package com.sm.di

import com.sm.domain.enums.Environment
import com.sm.domain.models.token.TokenConfig
import org.koin.dsl.module

val configModule =  module {

    single {
        TokenConfig(
            issuer = Environment.JWT_ISSUER.value,
            audience = Environment.JWT_AUDIENCE.value,
            secret = Environment.JWT_SECRET.value,
            expiresIn = 5 * 60 * 1000, //365L * 1000L * 60 * 60 * 24L for 1 year
        )
    }
}