package manager.task

import manager.task.models.User
import kotlin.reflect.KFunction1

annotation class WsEndpoint

class WS {

    data class IncomeMsg(
        val path: String,
        val auth: String,
        val reqId: String,
        /** поле инициализируется на беке, фронт не должен его отсылать.*/
        val activityId: Long = -1,
        /** JSON с фронта */
        val body: String
    )

    data class OutcomeMsg(
        val reqId: String,
        val activityId: Long,
        val success: Boolean,
        /** response ИЛИ error ИЛИ exception */
        val data: Any
    )

    data class Msg<T>(
        val body: T,
        val resp: Any,
        val err: Any
    )



    object Mappings {
        val mappings = mutableMapOf<String, PathHandler<Any>>()


         inline fun <reified T> setMapping(path: String, pathHandler: KFunction1<Any, Any>) {
            mappings[path] = PathHandler(T::class.java, pathHandler)
        }

        fun process(income: IncomeMsg) : Any {
            val pathHandler = mappings[income.path]
                ?: throw IllegalAccessError("""Mapping:"${income.path} is noy supported""")

            // TODO : короче здесь парсинг json → DTO
//            val pojo = Json.decodeFromString(pathHandler.argType, income.body)

            // TODO : это временно ↓
            val temp = income.body.split(",")
            val pojo = User(temp[0], temp[1], temp[2])

//            val t = pathHandler.argType!!::class.java
//            val pojo = Utils.gson.fromJson(income.body, pathHandler.argType!!::class.java)
//            return pathHandler.pathHandler.invoke(pojo as )
            return pathHandler.pathHandler.invoke(pojo)

        }
    }
    data class PathHandler<T>(val argType : Class<*>, val pathHandler: KFunction1<User, Any>)


    object Requests {
        private val requests = mutableMapOf<String, Pair<IncomeMsg, OutcomeMsg?>>()
        var nextActivityId : Long = 0
        get() = field++


        fun setIncome(reqId : String, incomeMsg: IncomeMsg) {
            requests[reqId] = Pair(incomeMsg, null)
        }

        // TODO : переписать на Красоту!
        fun setOutcome(reqId : String, outcomeMsg: OutcomeMsg) {
            // TODO : притащить логгер и заменить на warm
            System.err.println("request if not found by requestId: $reqId")
            val req = requests[reqId]
            if (req == null)  {System.err.println("request if not found by requestId: $reqId"); return}
            requests[reqId] = Pair(req.first, outcomeMsg)
        }
    }

}