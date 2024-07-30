package com.sm.routes

import com.sm.domain.enums.SocketMessageType
import com.sm.domain.models.signaling.SdpData
import com.sm.domain.models.signaling.SocketMessage
import com.sm.domain.models.user.socket.User
import com.sm.domain.models.user.socket.UserSocket
import com.sm.utils.AppUtils.getSocketMessageType
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

private val json = Json { ignoreUnknownKeys = true }

fun Routing.webSocketRouting(application: Application) {

    route("/signaling-server/{username}") {

        val connectedUserSockets = ConcurrentHashMap<String, UserSocket>()
        val connectedUsers = ConcurrentHashMap<String, User>()

        webSocket {

            val username = call.parameters["username"] ?: "unknown@mail.com"

            val socketSession = this

            try {

                connectedUserSockets[username] =
                    UserSocket(username = username, socket = socketSession)

                //Update user online status
                connectedUsers[username]?.let { user ->

                    val updatedUser = user.copy(isOnline = true)
                    connectedUsers[username] = updatedUser

                } ?: kotlin.run {
                    //Add new user online status
                    connectedUsers[username] = User(username = username, isOnline = true)
                }

                val connectionMessage = SocketMessage(
                    type = SocketMessageType.ClientConnection.type,
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

                        SocketMessageType.StartCall -> {
                            application.log.info("SocketMessageType.StartCall::$username - ${incomingData.target}")
                            val socketToCall = connectedUserSockets[incomingData.target ?: ""]

                            socketToCall?.let {
                                val socketMessage = SocketMessage(
                                    type = SocketMessageType.CallResponse.type,
                                    message = "user is ready",
                                    data = null,
                                )
                                socketSession.send(Frame.Text(json.encodeToString(socketMessage)))
                            } ?: kotlin.run {
                                val socketMessage = SocketMessage(
                                    type = SocketMessageType.CallResponse.type,
                                    message = "user is not ready",
                                    data = null,
                                )
                                socketSession.send(Frame.Text(json.encodeToString(socketMessage)))
                            }
                        }

                        SocketMessageType.CreateOffer -> {
                            application.log.info("SocketMessageType.CreateOffer::$username - ${incomingData.target}")
                            val socketToReceiveOffer =
                                connectedUserSockets[incomingData.target ?: ""]

                            val socketMessage = SocketMessage(
                                type = SocketMessageType.OfferReceived.type,
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            socketToReceiveOffer?.socket?.send(
                                Frame.Text(
                                    json.encodeToString(
                                        socketMessage
                                    )
                                )
                            )
                        }

                        SocketMessageType.CreateAnswer -> {
                            application.log.info("SocketMessageType.CreateAnswer::$username - ${incomingData.target}")
                            val socketToReceiveAnswer =
                                connectedUserSockets[incomingData.target ?: ""]

                            val socketMessage = SocketMessage(
                                type = SocketMessageType.AnswerReceived.type,
                                name = incomingData.name,
                                data = incomingData.data?.sdp,
                            )

                            socketToReceiveAnswer?.socket?.send(
                                Frame.Text(
                                    json.encodeToString(
                                        socketMessage
                                    )
                                )
                            )
                        }

                        SocketMessageType.CreateDecline -> {
                            application.log.info("SocketMessageType.CreateDecline::$username - ${incomingData.target}")
                            val socketToReceiveDecline =
                                connectedUserSockets[incomingData.target ?: ""]

                            val socketMessage = SocketMessage(
                                type = SocketMessageType.DeclineReceived.type,
                                name = incomingData.name,
                                data = null
                            )

                            socketToReceiveDecline?.socket?.send(
                                Frame.Text(
                                    json.encodeToString(
                                        socketMessage
                                    )
                                )
                            )
                        }

                        SocketMessageType.IceCandidate -> {
                            application.log.info("SocketMessageType.IceCandidate::$username - ${incomingData.target}")
                            val socketToReceiveIceCandidate =
                                connectedUserSockets[incomingData.target ?: ""]

                            val socketMessage = SocketMessage(
                                type = SocketMessageType.IceCandidate.type,
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
                                        socketMessage
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
                application.log.error("Error: ${e.localizedMessage}", e)
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
                        type = SocketMessageType.ClientDisconnection.type,
                        message = "$username disconnected",
                        data = connectedUsers.toList().map { it.second },
                    )
                    user.value.socket.send(Frame.Text(json.encodeToString(closeMessage)))
                }
            }
        }
    }
}