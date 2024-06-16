package com.sm.di

import com.sm.domain.interfaces.HashingService
import com.sm.domain.interfaces.TokenService
import com.sm.security.hashing.SHA256HashingService
import com.sm.security.token.JwtTokenService
import org.koin.dsl.module

val serviceModule =  module {

    single<TokenService> {
        JwtTokenService()
    }

    single<HashingService> {
        SHA256HashingService()
    }
}