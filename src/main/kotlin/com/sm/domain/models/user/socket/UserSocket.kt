package com.sm.domain.models.user.socket

import io.ktor.websocket.WebSocketSession

data class UserSocket(
    val username: String,
    val isOnline: Boolean,
    val socket: WebSocketSession,
)