package manager.task

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import manager.task.WS.Requests.setOutcome
import manager.task.exceptions.PathIsNotSupport
import manager.task.utils.Utils.gson
import manager.task.utils.log
import java.lang.reflect.Type
import java.time.LocalDateTime

suspend fun DefaultWebSocketServerSession.webSocketListeningMechanism() {
    send("You are connected! to Yurification! ^_^")
    for (frame in incoming) {
        // TODO : когда-то, понадобиться работать с файлами. тут есть Binary class
        frame as? Frame.Text ?: continue
        val wsTxt = frame.readText()
//        log.info("wsTxt", wsTxt)


        var reqId = "parse_failed"
        try {
            val (income, body) = parseIncome(wsTxt)
            reqId = income.reqId
            val activityId = WS.Requests.nextActivityId
            WS.Requests.setIncome(income.reqId, activityId, income)

            /* Либо упадём с Exception, либо пройдём дальше */
            val pathKeeper = WS.Paths[income.path]

            if (!passAuth(pathKeeper.isNeedAuth, income.auth)) {
                sendOutcome(WS.Outcome(reqId, false, "auth is not pass"))
                continue
            }

            income.body = gson.fromJson(body, pathKeeper.argType)
            val response: Any = pathKeeper.pathWalker.invoke(AppCtx, income.body)

            if (response !is Unit) sendOutcome(WS.Outcome(reqId, true, gson.toJson(response)))
        } catch (e: Throwable) {
            e.printStackTrace(System.err)
            val outcome = WS.Outcome(reqId, false, "${e::class.qualifiedName}(\"${e.message}\")")
            log.error("outcome: $outcome")
            sendOutcome(outcome)
        }
    }
}


private fun parseIncome(src: String): Pair<WS.Income, String> {
    val bodyIndex = src.indexOf("\"body\":")

    val info = src.substring(0, bodyIndex - 3) + "}"
    val body = src.substring(bodyIndex + 7, src.lastIndex - 1)

    return gson.fromJson(info, WS.Income::class.java) to body
}

private fun passAuth(isNeedAuth: Boolean, auth: String): Boolean {
    if (auth == "admin" || auth == "test") return true
    return if (isNeedAuth) isAuthVerified(auth) else true
}

private fun isAuthVerified(auth: String): Boolean {
    // TODO
    return true
}

private suspend fun DefaultWebSocketServerSession.sendOutcome(outcome: WS.Outcome) {
    setOutcome(outcome.reqId, outcome)
    send(gson.toJson(outcome))
}


class WS {

    data class Income(
        val reqId: String,
        val path: String,
        val auth: String,
        /** JSON с фронта */
        var body: Any
    )

    data class Outcome(
        val reqId: String,
        val success: Boolean,
        /** response ИЛИ error ИЛИ exception */
        val data: Any
    )


    object Paths {
        val mappings = mutableMapOf<String, PathKeeper>()

        operator fun get(path: String): PathKeeper = mappings[path]
            ?: throw PathIsNotSupport("Path: \"$path\" is not supported")


        inline fun <reified T> path(path: String, auth : Boolean = true, noinline pathWalker: AppCtx.(income: T) -> Any) {
            mappings[path] = PathKeeper(T::class.java, pathWalker as AppCtx.(Any) -> Any, auth)
        }

        data class PathKeeper(
            val argType: Type,
            val pathWalker: AppCtx.(Any) -> Any,
            val isNeedAuth: Boolean
        )
    }


    object Requests {
        private val log = log<Requests>()

        private val requests = mutableMapOf<String, RequestModel>()
        var nextActivityId: Long = 0
            get() = field++


        fun setIncome(reqId: String, activityId: Long, income: Income) {
            requests[reqId] = RequestModel(activityId).apply {
                this.income = income
                this.incomeTime = LocalDateTime.now()
            }
        }

        fun setOutcome(reqId: String, outcome: Outcome) {
            val model = requests[reqId]
            if (model == null) log.warm("request if not found by requestId: $reqId")
            else model.apply {
                this.outcome = outcome
                this.outcomeTime = LocalDateTime.now()
            }
        }

        private data class RequestModel(val activityId: Long) {
            lateinit var income: Income
            lateinit var outcome: Outcome
            lateinit var incomeTime: LocalDateTime
            lateinit var outcomeTime: LocalDateTime
        }
    }

}