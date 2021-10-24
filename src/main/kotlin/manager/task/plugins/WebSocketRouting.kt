package manager.task.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import manager.task.websocket.Connection
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureWebsocketRouting(): Collection<Connection> {

    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())

    routing {
        webSocket("/echo") {
            connections += Connection(this)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> send(Frame.Text(frame.readText()))
                }
            }
        }
    }

    return connections
}