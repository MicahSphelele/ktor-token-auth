package com.sm.extensions

import io.ktor.server.auth.jwt.*

val JWTPrincipal?.id get() = this?.getClaim("id", String::class) ?: ""
val JWTPrincipal?.email get() = this?.getClaim("email", String::class) ?: ""
