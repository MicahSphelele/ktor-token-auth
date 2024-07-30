package com.sm.domain.enums

enum class SocketMessageType(val type: String) {

    StartCall(type = "start_call"),
    CallResponse(type = "call_response"),
    CreateOffer(type ="create_offer"),
    OfferReceived(type = "offer_received"),
    CreateAnswer(type ="create_answer"),
    AnswerReceived(type = "answer_received"),
    CreateDecline(type ="create_decline"),
    DeclineReceived(type = "decline_received"),
    IceCandidate(type ="ice_candidate"),
    ClientConnection(type = "client_connection"),
    ClientDisconnection(type = "client_disconnection"),
    Random(type = "random"),
    Unknown(type = "unknown");

    fun isRandom(): Boolean = this == Random
}
