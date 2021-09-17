package manager.task

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import manager.task.configs.PsqlConfig
import manager.task.dao.memory.UserRepo
import manager.task.paths.userControllerPaths
import manager.task.services.AuthService
import manager.task.services.UserServ
import manager.task.utils.log


/*
### income example ###
{
"reqId":"1234-5678-9012",
"path":"c/user",
"auth":"admin",
"body":""
}
### outcome example ###
{
"reqId":"1234-5678-9012",
"activityId":0,
"success":true,
"data":""
}
*/
object AppCtx {
    /* Configs */
    val psqlConfig = PsqlConfig()
    lateinit var conf: PsqlConfig

    /* Repositories */
    val userRepo = UserRepo()

    /* Services */
    val userServ = UserServ()
    val authService = AuthService()
//    val authServicePoxy  = AuthServiceProxy()

}

fun AppCtx.init() {

}

fun main() {
    userControllerPaths()

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(WebSockets)
        routing {
            webSocket("/") {
                webSocketListeningMechanism()
            }
        }
    }.start(wait = true)
}

private val log = log<AppCtx>()

