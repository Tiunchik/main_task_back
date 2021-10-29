package manager.task

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import manager.task.plugins.*
import manager.task.services.DataBaseWarden
import manager.task.websocket.applyWebSocketModule

fun main() {

    val dataBase = DataBaseWarden()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        applyWebSocketModule()

        configureHttpRouting()
        configureWebsocketRouting(dataBase.filmActions)
    }.start(wait = true)
}