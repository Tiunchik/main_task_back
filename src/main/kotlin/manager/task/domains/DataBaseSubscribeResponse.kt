package manager.task.domains

import com.fasterxml.jackson.databind.JsonNode

data class DataBaseSubscribeResponse(
    val tableName: String,
    val value: JsonNode
) {



}
