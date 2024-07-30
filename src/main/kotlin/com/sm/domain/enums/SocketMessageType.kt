package com.sm.domain.enums

enum class SocketMessageType(val type: String) {

    StartCall(type = "start_call"),
    CreateOffer(type ="create_offer"),
    CreateAnswer(type ="create_answer"),
    CreateDecline(type ="create_decline"),
    IceCandidate(type ="ice_candidate"),
    Random(type = "random"),
    Unknown(type = "unknown");

    fun isStartCall(): Boolean = this == StartCall

    fun isCreateOffer(): Boolean = this == CreateOffer

    fun isCreateAnswer(): Boolean = this == CreateAnswer

    fun isIceCandidate(): Boolean = this == IceCandidate

    fun isRandom(): Boolean = this == Random
}
