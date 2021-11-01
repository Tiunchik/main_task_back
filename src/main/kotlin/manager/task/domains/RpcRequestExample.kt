package manager.task.domains

import java.util.*

data class RpcRequestExample(
    val method: String,
    val parameters: String,
    val id: UUID
)
