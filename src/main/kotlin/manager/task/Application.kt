package manager.task

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import manager.task.dao.memory.UserRepo
import manager.task.models.User
import kotlin.reflect.KFunction1

/**
 * TODO : error pojo - единая структура для всех error
 * TODO : посмотреть примеры Идеальной архитектуры ktor
 * TODO : авто-парсинг JSON в POJO ожидаемый методов который обрабатывает path
 * TODO : ? postman workspace ?
 * TODO : решить вопрос с @Serializable VS Gson
 * TODO : Почитать про data class + kotlin reflection
 * TODO :
 * TODO :
 * TODO :
 *
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        buildAppContext {
            userRepo = UserRepo()
        }
        buildMappings {
            WS.Mappings.setMapping<User>("c/user", userRepo::userCreate as KFunction1<Any, Any>)
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


/**
 * Тест для postman
 * 127.0.0.1:8080

c/user
auth
reqId
nick,login,password

 *
 */
fun Application.module() {
    install(WebSockets)
    routing {
        webSocket("/") {
            send("You are connected! to Yurification! ^_^")
            for (frame in incoming) {
                // TODO : когда-то, понадобиться работать с файлами. тут есть Binary class
                frame as? Frame.Text ?: continue
                val wsMsg = frame.readText().split("\r\n")

                println(wsMsg)

                val income = WS.IncomeMsg(
                    path = wsMsg[0], auth = wsMsg[1],
                    reqId = wsMsg[2], body = wsMsg[3],
                    activityId = WS.Requests.nextActivityId
                )
                WS.Requests.setIncome(income.reqId, income)
                System.err.println("income: $income")

                var outcome : WS.OutcomeMsg
                try {
                    val response = WS.Mappings.process(income)
                    outcome = WS.OutcomeMsg(
                        reqId = income.reqId,
                        activityId = income.activityId,
                        success = true,
                        data = Utils.gson.toJson(response)
                        )
                } catch (e : Throwable) {
                    e.printStackTrace(System.err)
                    outcome = WS.OutcomeMsg(
                        reqId = income.reqId,
                        activityId = income.activityId,
                        success = false,
                        data = "${e::class.qualifiedName}(\"${e.message}\")"
                    )
                }

                System.err.println("outcome: $outcome")
                send(Utils.gson.toJson(outcome))
            }
        }
    }
}









