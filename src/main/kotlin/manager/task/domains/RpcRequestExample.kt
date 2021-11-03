package manager.task.domains

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class RpcRequestExample(
    val method: String,
    val parameters: JsonNode,
    val id: UUID
)
