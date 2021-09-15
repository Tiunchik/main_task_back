package manager.task

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*

import manager.task.configs.PsqlConfig
import manager.task.dao.memory.UserRepo
import manager.task.paths.userControllerPaths
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

    /* Repositories */
    val userRepo = UserRepo()
    val userServ = UserServ()

    /* Services */
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
val log = log<AppCtx>()

//fun main() {
////    val t = gson.toJson(WS.Income("c/user", "admin", "1234-5678-9012", 0, ""))
////    val t = gson.toJson(WS.Outcome("1234-5678-9012", 0, true, ""))
//
//    val src = """
//        {
//        "reqId":"1234-5678-9012",
//        "path":"c/user",
//        "auth":"admin",
//        "body":{
//        "reqId":"1234-5678-9012",
//        "path":"c/user",
//        "auth":"admin",
//        "body":""
//        }
//        }
//    """.trimIndent()
//
////    val incomeInfo = parseIncomeInfo(src)
////    log<AppCtx>().info("body", incomeInfo)
////    val y = gson.fromJson(incomeInfo, WS.Income::class.java)
////    println(y)
////    println(y.body::class.simpleName)
////    println(t)
//
////    val (income, body) = parseIncome(src)
////    log.info("income", income)
////    log.info("body", body)
//}









