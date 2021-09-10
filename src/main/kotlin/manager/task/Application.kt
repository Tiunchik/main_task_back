package manager.task

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import manager.task.ws.WS

/**
 * TODO : error pojo - единая структура для всех error
 * TODO : посмотреть примеры Идеальной архитектуры ktor
 * TODO : авто-парсинг JSON в POJO ожидаемый методов который обрабатывает path
 * TODO : ? postman workspace ?
 * TODO : решить вопрос с @Serializable
 * TODO :
 *
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        buildAppContext {
            userRepo = UserRepo()
        }
        buildMappings {
            WS.Mappings.setMapping("c/user", userRepo::userCreate)
        }
        module()
    }.start(wait = true)
}

object AppContext {
    lateinit var userRepo: UserRepo
}

fun buildAppContext(dsl: AppContext.() -> Unit) {
    dsl.invoke(AppContext)
}

fun buildMappings(dsl: AppContext.() -> Unit) {
    dsl.invoke(AppContext)
}

fun Application.module() {
    install(WebSockets)
    routing {
        webSocket("/") {
            send("You are connected! to Yurification! ^_^")
            for (frame in incoming) {
                // TODO : когда-то, понадобиться работать с файлами. тут есть Binary class
                frame as? Frame.Text ?: continue
                val wsMsg = frame.readText().split("\r\n")


                val income = WS.IncomeMsg(
                    path = wsMsg[0], auth = wsMsg[1],
                    reqId = wsMsg[2], body = wsMsg[3],
                    activityId = WS.Requests.nextActivityId
                )
                WS.Requests.setIncome(income.reqId, income)
                println("income: $income")

                val hz4to = WS.Mappings.process(income)

                send("")
            }
        }
    }
}


class UserRepo {
    private val userStore = mutableListOf<User>()

    fun userCreate(user: User): Any {
        // TODO : сделать парсинг в параметров для wsMsg.
        userStore += user
        return ""
    }

    fun userRead(lines: List<String>): Any {
        val searchParam = lines[1]
        userStore.forEach { if (it.matchAnyField(searchParam)) return it }
        return ""
    }
}

// TODO : kotlinx.serialization
@Serializable
data class User(val nick: String, val login: String, val password: String)

fun User.matchAnyField(search: Any): Boolean {
    if (search is String) {
        if (nick.contains(search)) return true
        if (login.contains(search)) return true
        if (password.contains(search)) return true
    }
    return false
}









