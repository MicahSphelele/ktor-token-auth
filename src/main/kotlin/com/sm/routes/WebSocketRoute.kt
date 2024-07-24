package com.sm.routes

import com.sm.domain.enums.SocketMessageType
import com.sm.domain.models.signaling.SdpData
import com.sm.domain.models.signaling.SocketMessage
import com.sm.domain.models.user.socket.User
import com.sm.domain.models.user.socket.UserSocket
import com.sm.utils.AppUtils.getSocketMessageType
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val json = Json { ignoreUnknownKeys = true }

fun Routing.webSocketRouting() {

    route("/signaling-server/{username}") {

        val connectedUserSockets = ConcurrentHashMap<String, UserSocket>()
        val connectedUsers = ConcurrentHashMap<String, User>()

        webSocket {

            val username = call.parameters["username"] ?: "unknown@mail.com"

            val socketSession = this

            try {

                connectedUserSockets[username] = UserSocket(username = username, isOnline = true, socket = socketSession)

                //Update user online status
                connectedUsers[username]?.let { user ->

                    val updatedUser= user.copy(isOnline = true)
                    connectedUsers[username] = updatedUser

                } ?: kotlin.run {
                    //Add new user online status
                    connectedUsers[username] = User(username = username, isOnline = true)
                }

                val connectionMessage = SocketMessage(
                    type = "client_connection",
                    message = "$username connected",
                    data = connectedUsers.toList().map { it.second },
                )

                connectedUserSockets.forEach { socket ->
                    socket.value.socket.send(Frame.Text(text = json.encodeToString(connectionMessage)))
                }

                for (frame in incoming) {

                    frame as? Frame.Text ?: continue
                    val text = frame.readText()

                    val incomingData = json.decodeFromString<SocketMessage<SdpData>>(text)

                    when (val type = getSocketMessageType(incomingData.type)) {

                        SocketMessageType.START_CALL -> {

                            val socketToCall = connectedUserSockets[incomingData.target ?: ""]

                            socketToCall?.let {
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

                            val socketToReceiveOffer = connectedUserSockets[incomingData.target ?: ""]

                            val message = SocketMessage(
                                type = "offer_received",
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            socketToReceiveOffer?.socket?.send(Frame.Text(json.encodeToString(message)))
                        }

                        SocketMessageType.CREATE_ANSWER -> {

                            val socketToReceiveAnswer = connectedUserSockets[incomingData.target ?: ""]

                            val message = SocketMessage(
                                type = "answer_received",
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            socketToReceiveAnswer?.socket?.send(Frame.Text(json.encodeToString(message)))
                        }

                        SocketMessageType.ICE_CANDIDATE -> {

                            val socketToReceiveIceCandidate = connectedUserSockets[incomingData.target ?: ""]

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

                            socketToReceiveIceCandidate?.socket?.send(
                                Frame.Text(
                                    json.encodeToString(
                                        message
                                    )
                                )
                            )
                        }

                        else -> {

                            if (type.isRandom()) {

                                if (connectedUserSockets.isNotEmpty()) {

                                    connectedUserSockets.forEach { user ->

                                        val closeMessage = SocketMessage<String>(
                                            type = "random",
                                            message = "$username says ${incomingData.message}",
                                        )
                                        if (user.key != username) {
                                            user.value.socket.send(
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

                //Update user online status
                val connectedUser = connectedUsers[username]?.copy(isOnline = false)

                connectedUser?.let { user ->
                    connectedUsers[username] = user
                }

                //Remove socket completely to avoid memory leaks and connection issues
                connectedUserSockets.remove(username)

                //Send disconnection message to all connected users
                connectedUserSockets.forEach { user ->

                    val closeMessage = SocketMessage(
                        type = "client_disconnection",
                        message = "$username disconnected",
                        data = connectedUsers.toList().map { it.second },
                    )
                    user.value.socket.send(Frame.Text(json.encodeToString(closeMessage)))
                }
            }
        }
    }
}