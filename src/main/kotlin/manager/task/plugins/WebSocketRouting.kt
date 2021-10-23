package manager.task.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import manager.task.websocket.SystemConnection
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureWebsocketRouting() {

    val connections = Collections.synchronizedSet<SystemConnection>(LinkedHashSet())

    routing {
        webSocket("/echo") {
            connections += SystemConnection(this)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> send(Frame.Text(frame.readText()))
                }
            }
        }
    }
}