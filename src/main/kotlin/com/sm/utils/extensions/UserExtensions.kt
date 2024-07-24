package com.sm.utils.extensions

import com.sm.domain.models.user.socket.User
import com.sm.domain.models.user.socket.UserSocket

fun UserSocket.toUser(): User = User(
    username = username,
    isOnline = isOnline,
)