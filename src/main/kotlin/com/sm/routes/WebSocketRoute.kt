package com.sm.routes

import com.sm.domain.enums.SocketMessageType
import com.sm.domain.models.signaling.SdpData
import com.sm.domain.models.signaling.SocketMessage
import com.sm.utils.AppUtils.getSocketMessageType
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val json = Json { ignoreUnknownKeys = true }

fun Routing.webSocketRouting() {

    route("/signaling-server/{username}") {

        val connectedSockets = ConcurrentHashMap<String, WebSocketSession>()

        webSocket {

            val username = call.parameters["username"] ?: "unknown@mail.com"

            val socketSession = this

            try {

                connectedSockets[username] = socketSession

                val introMessage = SocketMessage(
                    type = "intro",
                    message = "User connected: $username",
                    data = connectedSockets.toList().map { it.first },
                )

                connectedSockets.forEach { socket ->
                    socket.value.send(Frame.Text(text = json.encodeToString(introMessage)))
                }

                for (frame in incoming) {

                    frame as? Frame.Text ?: continue
                    val text = frame.readText()

                    val incomingData = json.decodeFromString<SocketMessage<SdpData>>(text)

                    when (val type = getSocketMessageType(incomingData.type)) {

                        SocketMessageType.START_CALL -> {

                            val userToCall = connectedSockets[incomingData.target ?: ""]

                            userToCall?.let {
                                val message = SocketMessage(
                                    type = "call_response",
                                    data = "user is ready for a call",
                                )
                                socketSession.send(Frame.Text(json.encodeToString(message)))
                            } ?: kotlin.run {
                                val message = SocketMessage(
                                    type = "call_response",
                                    data = "user is not online",
                                )
                                socketSession.send(Frame.Text(json.encodeToString(message)))
                            }
                        }

                        SocketMessageType.CREATE_OFFER -> {

                            val userToReceiveOffer = connectedSockets[incomingData.target ?: ""]

                            val message = SocketMessage(
                                type = "offer_received",
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            userToReceiveOffer?.send(Frame.Text(json.encodeToString(message)))
                        }

                        SocketMessageType.CREATE_ANSWER -> {

                            val userToReceiveAnswer = connectedSockets[incomingData.target ?: ""]

                            val message = SocketMessage(
                                type = "answer_received",
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            userToReceiveAnswer?.send(Frame.Text(json.encodeToString(message)))
                        }

                        SocketMessageType.ICE_CANDIDATE -> {

                            val userToReceiveIceCandidate =
                                connectedSockets[incomingData.target ?: ""]

                            val message = SocketMessage(
                                type = "ice_candidate",
                                name = incomingData.name,
                                data = SdpData(
                                    sdp = incomingData.data?.sdp,
                                    sdpMLineIndex = incomingData.data?.sdpMLineIndex,
                                    sdpMid = incomingData.data?.sdpMid,
                                    sdpCandidate = incomingData.data?.sdpCandidate,
                                ),
                            )

                            userToReceiveIceCandidate?.send(Frame.Text(json.encodeToString(message)))
                        }

                        else -> {

                            if (type.isRandom()) {

                                if (connectedSockets.isNotEmpty()) {

                                    connectedSockets.forEach { socket ->

                                        val closeMessage = SocketMessage<String>(
                                            type = "random",
                                            message = "$username says ${incomingData.message}",
                                        )
                                        if (socket.key != username) {
                                            socket.value.send(
                                                Frame.Text(
                                                    json.encodeToString(
                                                        closeMessage
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                println("Error: ${e.localizedMessage}")
            } finally {

                connectedSockets.remove(username)

                connectedSockets.forEach { socket ->

                    val closeMessage = SocketMessage(
                        type = "close",
                        message = "User disconnected: $username",
                        data = connectedSockets.toList().map { it.first },
                    )
                    socket.value.send(Frame.Text(json.encodeToString(closeMessage)))
                }
            }
        }
    }
}