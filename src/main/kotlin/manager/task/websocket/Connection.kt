package manager.task.websocket

import io.ktor.http.cio.websocket.*

data class Connection(val session: DefaultWebSocketSession) : Comparable<Connection> {

    override fun compareTo(other: Connection) = this.session.toString().compareTo(other.session.toString())

}
