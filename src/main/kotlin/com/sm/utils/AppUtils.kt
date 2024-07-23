package com.sm.utils

import com.sm.domain.enums.SocketMessageType

object AppUtils {

    fun getSocketMessageType(type: String): SocketMessageType = when(type) {
        "start_call" -> SocketMessageType.START_CALL
        "create_offer" -> SocketMessageType.CREATE_OFFER
        "create_answer" -> SocketMessageType.CREATE_ANSWER
        "ice_candidate" -> SocketMessageType.ICE_CANDIDATE
        "random" -> SocketMessageType.RANDOM
        else -> SocketMessageType.UNKNOWN
    }
}