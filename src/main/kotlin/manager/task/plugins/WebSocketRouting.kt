package manager.task.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import manager.task.Context
import manager.task.models.RpcRequestExample
import manager.task.models.WebSocketSubscribeRequest
import manager.task.enums.Method
import manager.task.websocket.Connection
import java.util.concurrent.ConcurrentSkipListSet

fun Application.configureWebsocketRouting() {

    val objectMapper = ObjectMapper()
        .registerModule(KotlinModule())

    val enumMap = Method.values().asIterable().associateBy { it.name } //inject()

    routing {
        webSocket("/methods") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> objectMapper.readValue<RpcRequestExample>(frame.readText())
                        .let { enumMap[it.method]?.service?.doRequest(it, this) }
                }
            }
        }

        webSocket("/subscribe") {
            Context.filmsSubscriber += Connection(this@webSocket)
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        objectMapper.readValue<WebSocketSubscribeRequest>(frame.readText()).let { request ->
                            Context.mapSubscribers
                                .getOrDefault(request.tableName, ConcurrentSkipListSet())
                                .apply { add(Connection(this@webSocket)) }
                                .also { Context.mapSubscribers[request.tableName] = it }
                        }
                    }
                    is Frame.Binary -> this.outgoing.close()
                }
            }
        }
    }
}
