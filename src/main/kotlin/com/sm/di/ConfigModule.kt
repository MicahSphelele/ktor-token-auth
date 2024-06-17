package com.sm.di

import com.sm.domain.models.token.TokenConfig
import com.sm.util.Constants
import com.sm.util.Constants.JWT_AUDIENCE
import com.sm.util.Constants.JWT_ISSUER
import com.sm.util.Constants.JWT_SECRET
import org.koin.dsl.module

val configModule =  module {

    single {
        TokenConfig(
            issuer = JWT_ISSUER,
            audience = JWT_AUDIENCE,
            secret = JWT_SECRET,
            expiresIn = 5 * 60 * 1000, //365L * 1000L * 60 * 60 * 24L for 1 year
        )
    }
}