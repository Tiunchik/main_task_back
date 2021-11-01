package manager.task

import io.ktor.util.collections.*
import manager.task.repositories.Config
import manager.task.websocket.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.collections.LinkedHashSet

object Context {

    val dbConfiguration = Config()

    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    val filmsSubscriber = Collections.synchronizedSet<Connection>(LinkedHashSet())

    val mapSubscribers = ConcurrentHashMap<String, ConcurrentSkipListSet<Connection>>()
}
