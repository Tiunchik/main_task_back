package manager.task

import manager.task.repositories.Config
import manager.task.websocket.Connection
import java.util.*
import kotlin.collections.LinkedHashSet

object Context {

    val dbConfiguration = Config()

    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    val filmsSubscriber = Collections.synchronizedSet<Connection>(LinkedHashSet())

}