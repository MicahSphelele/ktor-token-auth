package com.sm.utils

import com.sm.domain.enums.SocketMessageType

object AppUtils {

    fun getSocketMessageType(type: String): SocketMessageType = when(type) {
        "start_call" -> SocketMessageType.StartCall
        "create_offer" -> SocketMessageType.CreateOffer
        "create_answer" -> SocketMessageType.CreateAnswer
        "ice_candidate" -> SocketMessageType.IceCandidate
        "random" -> SocketMessageType.Random
        else -> SocketMessageType.Unknown
    }
}