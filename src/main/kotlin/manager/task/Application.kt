package manager.task

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
//        configureRouting()
        module()
    }.start(wait = true)
}


fun Application.module() {
    install(io.ktor.websocket.WebSockets)
    routing {
        webSocket("/") {
            send("You are connected!")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                send("You said: $receivedText")
            }
        }
        webSocket("/some") {
            send("You are amazing!")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                send("You said from some: $receivedText")
            }
        }
    }
}
