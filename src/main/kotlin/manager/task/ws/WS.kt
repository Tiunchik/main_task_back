package manager.task.ws

import com.typesafe.config.Optional
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import manager.task.User
import kotlin.reflect.KClass

class WS {

    data class IncomeMsg(
        val path: String,
        val auth: String,
        val reqId: String,
        /** поле инициализируется на беке, фронт не должен его отсылать.*/
        @Optional val activityId: Long = -1,
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
        val mappings = mutableMapOf<String, Pair<KClass<*>, (arg: Any) -> Any>>()


         inline fun <reified T> setMapping(path: String, noinline handle: (arg: Any) -> Any) {
            mappings[path] = Pair(T::class, handle)
        }

        fun process(income: IncomeMsg) {
            val j = mappings[income.path]
            val pojo = Json.decodeFromString<User>(income.body)
        }
    }
    data class Mapping(val argType : KClass<*>)

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