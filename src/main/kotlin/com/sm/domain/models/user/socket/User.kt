package com.sm.domain.models.user.socket

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val isOnline: Boolean,
)
