package manager.task

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import manager.task.plugins.*
import manager.task.websocket.applyWebSocketModule

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureHttpRouting()
        applyWebSocketModule()
        configureWebsocketRouting()
    }.start(wait = true)
}