package manager.task.models

import com.fasterxml.jackson.databind.JsonNode

data class DataBaseSubscribeResponse(
    val tableName: String,
    val value: JsonNode
) {



}
