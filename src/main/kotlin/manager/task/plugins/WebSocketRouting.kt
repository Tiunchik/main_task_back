package manager.task.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import manager.task.Context
import manager.task.websocket.Connection
import reactor.core.Disposable
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureWebsocketRouting() {

    routing {
        webSocket("/echo") {
            Context.connections += Connection(this)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> send(Frame.Text(frame.readText()))
                }
            }
        }

        webSocket("/films/subscribe") {
            Context.filmsSubscriber += Connection(this)
        }
    }
}
