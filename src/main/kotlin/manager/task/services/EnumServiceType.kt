package manager.task.services

import io.ktor.http.cio.websocket.*
import manager.task.models.RpcRequestExample

interface EnumServiceType {

    fun doRequest(request: RpcRequestExample, session: DefaultWebSocketSession)

}
