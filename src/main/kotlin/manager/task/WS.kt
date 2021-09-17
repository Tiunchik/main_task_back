package manager.task

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import manager.task.WS.Requests.setOutcome
import manager.task.exceptions.ConfigurationException
import manager.task.exceptions.JsonParseException
import manager.task.exceptions.PathIsNotSupported
import manager.task.utils.Utils
import manager.task.utils.Utils.gson
import manager.task.utils.log
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

private const val PARSE_FAIL = "parse_failed"
private val log = log<WS>()
/**
 * Exceptions:
 * JsonParseException - не удалось распарсить полученный текст из ws -> WS.Income(...)
 * PathIsNotSupported - income.path не найдет среди всех добавленных paths
 * JsonParseException - не удалось распарсить income.body -> ожидаемый тип аргумента для path
 * RuntimeException - всё что упало походу исполнения pathWalker
 */
suspend fun DefaultWebSocketServerSession.webSocketListeningMechanism() {
    send("You are connected! to Yurification! ^_^")
    for (frame in incoming) {
        // TODO : когда-то, понадобиться работать с файлами. тут есть Binary class
        frame as? Frame.Text ?: continue

        var reqId = PARSE_FAIL
        var pathKeeper : WS.Paths.PathKeeper = WS.Paths.ExceptionPathKeeper
        try {
            /* упадём с JsonParseException(...) или пройдём дальше */
            val (income, rawJsonBody) = parseIncome(frame.readText())
            reqId = income.reqId
            val activityId = WS.Requests.getNextActivityId()
            WS.Requests.setIncome(income.reqId, activityId, income)

            /* упадём с PathIsNotSupported или пройдём дальше */
            pathKeeper = WS.Paths[income.path]

            if (pathKeeper.isNeedAuth && !AppCtx.authService.checkAuth(income.auth)) {
                sendOutcome(WS.Outcome(reqId, false, "auth is not pass"))
                continue
            }

            /* упадём с JsonParseException(...) или пройдём дальше */
            val response: Any = if (pathKeeper.expectedParamType.typeName == "kotlin.Unit") {
                pathKeeper.walk(Unit, activityId, income.auth)
            } else {
                income.body = Utils.fromJson(rawJsonBody, pathKeeper.expectedParamType) ?:
                 throw JsonParseException("Unexpected income.body=\"${income.body}\"")
                pathKeeper.walk(income.body, activityId, income.auth)
            }

            /* если pathWalker ничего не вернул, то мы и не будет что-то отправлять. */
            if (response !is Unit) sendOutcome(WS.Outcome(reqId, true, gson.toJson(response)))
        } catch (e: Throwable) {
            val outcome = WS.Outcome(reqId, false, "${e::class.qualifiedName}(\"${e.message}\")")
            log.error("Path=\"${pathKeeper.path}\" reqId=\"$reqId\" exception: $outcome")
            e.printStackTrace(System.err)
            sendOutcome(outcome)
        }
    }
}


private fun parseIncome(src: String): Pair<WS.Income, String> {
    val bodyIndex = src.indexOf("\"body\":")

    val info = src.substring(0, bodyIndex - 3) + "}"
    val body = src.substring(bodyIndex + 7, src.lastIndex - 1)

    return Utils.fromJson<WS.Income>(info) to body
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
        private val paths = mutableMapOf<String, PathKeeper>()

        operator fun get(path: String): PathKeeper = paths[path]
            ?: throw PathIsNotSupported(path)

        operator fun set(path: String, pathKeeper: PathKeeper) {
            if (paths.containsKey(path)) throw ConfigurationException("Try to add duplicate for path:\"$path\"")
            paths[path] = pathKeeper
        }

        inline fun <reified T> path(path: String, auth: Boolean = true, noinline pathWalker: AppCtx.(income: T) -> Any?) {
            this[path] = PathKeeperSingle(path, T::class.java, auth, pathWalker as AppCtx.(Any) -> Any)
        }

        inline fun <reified T> pathCtx(path: String, auth: Boolean = true, noinline pathWalker: AppCtx.(income: T, ctx: PathCtx) -> Any?) {
            this[path] = PathKeeperCtx(path, T::class.java, auth, pathWalker as AppCtx.(Any, PathCtx) -> Any)
        }

        interface PathKeeper {
            val path: String
            val expectedParamType: Type
            val isNeedAuth: Boolean

            fun walk(param: Any, activityId: Long, auth: String): Any
        }

        class PathKeeperSingle(
            override val path: String,
            override val expectedParamType: Type,
            override val isNeedAuth: Boolean,
            private val pathWalker: AppCtx.(Any) -> Any
        ) : PathKeeper {
            override fun walk(param: Any, activityId: Long, auth: String) = pathWalker.invoke(AppCtx, param)
        }

        class PathKeeperCtx(
            override val path: String,
            override val expectedParamType: Type,
            override val isNeedAuth: Boolean,
            private val pathWalker: AppCtx.(Any, PathCtx) -> Any
        ) : PathKeeper {
            override fun walk(param: Any, activityId: Long, auth: String) {
                pathWalker.invoke(AppCtx, param, PathCtx(auth, activityId, path, isNeedAuth))
            }
        }

        object ExceptionPathKeeper : PathKeeper {
            override val path: String = "unknown path"
            override val expectedParamType: Type
                get() = TODO("Not yet implemented")
            override val isNeedAuth: Boolean
                get() = TODO("Not yet implemented")
            override fun walk(param: Any, activityId: Long, auth: String): Any = TODO("Not yet implemented")
        }

        data class PathCtx(
            val auth: String,
            val activityId: Long,
            private val path: String,
            private val isNeedAuth: Boolean

        ) {
            val currentUser by lazy {
                if (!isNeedAuth) throw ConfigurationException(
                    "Path:\"$path\" has auth==false. " +
                            "You try to get currentUser in this pathWalker! It's IMPOSSIBLE " +
                            "Try to remove code which required currentUser " +
                            "or set auth=true (this path will require auth from Income)"
                )
                AppCtx.authService.getCurrentUser(auth)
            }
        }

    }


    /** Только для логгирования и дебага */
    object Requests {
        private val log = log<Requests>()

        private val requests = mutableMapOf<String, RequestModel>()
        private var nextActivityId: AtomicLong = AtomicLong(0)

        fun getNextActivityId() = nextActivityId.getAndIncrement()

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