package com.sm.domain.models.signaling

import kotlinx.serialization.Serializable

@Serializable
data class SocketMessage<T>(
    val type: String,
    val name: String? = null,
    val message: String? = null,
    val data: T? = null,
    val target: String? = null
)