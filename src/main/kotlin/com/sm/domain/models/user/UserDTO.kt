package com.sm.domain.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val firstName: String,
    val lastName: String,
    val email: String
)
