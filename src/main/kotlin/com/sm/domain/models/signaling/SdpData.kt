package com.sm.domain.models.signaling

import kotlinx.serialization.Serializable

@Serializable
data class SdpData(
    val sdp: String? = null,
    val sdpMLineIndex: String? = null,
    val sdpMid: String? = null,
    val sdpCandidate: String? = null
)
