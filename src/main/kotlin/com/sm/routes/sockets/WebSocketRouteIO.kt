package com.sm.routes.sockets

import com.sm.utils.SessionManager
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.UUID

fun Routing.webSocketRoutingIO(application: Application) {
    route("/rtc") {

        webSocket {

            val sessionID = UUID.randomUUID()

            try {

                SessionManager.onSessionStarted(sessionId = sessionID, session = this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            SessionManager.onMessage(sessionID, frame.readText())
                        }

                        else -> Unit
                    }
                }
                application.log.info("Exiting incoming loop, closing session: $sessionID")
                SessionManager.onSessionClose(sessionID)
            } catch (e: ClosedReceiveChannelException) {
                SessionManager.onSessionClose(sessionID)
                application.log.error("onClose $sessionID")
            } catch (e: Throwable) {
                SessionManager.onSessionClose(sessionID)
                application.log.error("onError $sessionID", e)
            }
        }



    }
}