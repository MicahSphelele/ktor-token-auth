package com.sm.utils

import com.sm.domain.enums.SocketMessageType

object AppUtils {

    fun getSocketMessageType(type: String): SocketMessageType = when(type) {
        "start_call" -> SocketMessageType.StartCall
        "call_response" -> SocketMessageType.CallResponse
        "create_offer" -> SocketMessageType.CreateOffer
        "offer_received" -> SocketMessageType.OfferReceived
        "create_answer" -> SocketMessageType.CreateAnswer
        "answer_received" -> SocketMessageType.AnswerReceived
        "ice_candidate" -> SocketMessageType.IceCandidate
        "create_decline" -> SocketMessageType.CreateDecline
        "decline_received" -> SocketMessageType.DeclineReceived
        "create_end_call" -> SocketMessageType.CreateEndCall
        "end_call_received" -> SocketMessageType.EndCallReceived
        "random" -> SocketMessageType.Random
        else -> SocketMessageType.Unknown
    }
}