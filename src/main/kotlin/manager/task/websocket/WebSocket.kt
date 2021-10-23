package manager.task.websocket

import io.ktor.application.*
import io.ktor.websocket.*

fun Application.applyWebSocketModule() {
    install(WebSockets)
}