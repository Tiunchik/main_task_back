package manager.task.plugins

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

}

//fun Application.module() {
//    install(WebSockets)
//    routing {
//        webSocket("/chat") {
//            send("You are connected!")
//            for(frame in incoming) {
//                frame as? Frame.Text ?: continue
//                val receivedText = frame.readText()
//                send("You said: $receivedText")
//            }
//        }
//    }
//}