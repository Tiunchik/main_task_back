package manager.task

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import manager.task.common.commonPath
import manager.task.common.examplePaths
import manager.task.common.log
import manager.task.common.webSocketListeningMechanism
import manager.task.configs.PsqlConfig
import manager.task.domains.user.UserRepo
import manager.task.domains.user.UserServ
import manager.task.domains.user.userControllerPaths
import manager.task.microservices.AuthService


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


/*
127.0.0.1:8080 <- вставлять в postman

### income example ###
{
"reqId":"1234-5678-9012",
"path":"printPathConfig",
"auth":"admin",
"body":
}
### outcome example ###
{
"reqId":"1234-5678-9012",
"activityId":0,
"success":true,
"data":""
}
*/
fun main() {
    userControllerPaths()
    examplePaths()
    commonPath()

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(WebSockets)
        routing {
            webSocket("/") {
                send("You are connected! to Yurification! ^_^")
                webSocketListeningMechanism()
            }
        }
    }.start(wait = true)
}

private val log = log<AppCtx>()

