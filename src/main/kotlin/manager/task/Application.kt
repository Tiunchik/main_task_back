package manager.task

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import manager.task.plugins.*
import manager.task.services.DataBaseWarden
import manager.task.websocket.applyWebSocketModule

fun main() {



    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("manager.task.**")) {
        applyWebSocketModule()

        configureHttpRouting()
        configureWebsocketRouting()
        val dataBase = DataBaseWarden(log)
        dataBase.createListener()
    }.start(wait = true)

}
