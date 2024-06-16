package com.sm.di

import com.sm.domain.models.token.TokenConfig
import org.koin.dsl.module

val configModule =  module {

    single {
        TokenConfig(
            issuer = System.getenv("jwt.issuer") ?: "",
            audience = System.getenv("jwt.audience") ?: "",
            expiresIn = 5 * 60 * 1000, //365L * 1000L * 60 * 60 * 24L for 1 year
            secret = System.getenv("JWT_SECRET") ?: ""
        )
    }
}