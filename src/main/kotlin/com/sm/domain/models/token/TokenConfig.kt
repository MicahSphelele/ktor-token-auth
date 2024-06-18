package com.sm.domain.models.token

data class TokenConfig(
    val issuer: String = "",
    val audience: String = "",
    val secret: String = "",
    val subject: String = "",
    val expiresIn: Long = 0
)