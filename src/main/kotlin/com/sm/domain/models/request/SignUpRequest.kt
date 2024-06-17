package com.sm.domain.models.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    var password: String,
)