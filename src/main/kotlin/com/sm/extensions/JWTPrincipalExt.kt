package com.sm.extensions

import io.ktor.server.auth.jwt.*

val JWTPrincipal?.userId get() = this?.getClaim("id", String::class) ?: ""
val JWTPrincipal?.userRole get() = this?.getClaim("role", String::class) ?: ""
val JWTPrincipal?.userEmail get() = this?.getClaim("email", String::class) ?: ""
val JWTPrincipal?.tokenType get() = this?.getClaim("tokenType", String::class) ?: ""
