package com.sm.domain.enums

enum class SocketMessageType(val type: String) {

    START_CALL(type = "start_call"),
    CREATE_OFFER(type ="create_offer"),
    CREATE_ANSWER(type ="create_answer"),
    ICE_CANDIDATE(type ="ice_candidate"),
    RANDOM(type = "random"),
    UNKNOWN(type = "unknown");

    fun isStartCall(): Boolean = this == START_CALL

    fun isCreateOffer(): Boolean = this == CREATE_OFFER

    fun isCreateAnswer(): Boolean = this == CREATE_ANSWER

    fun isIceCandidate(): Boolean = this == ICE_CANDIDATE

    fun isRandom(): Boolean = this == RANDOM
}
