package manager.task.services

import io.ktor.http.cio.websocket.*
import io.r2dbc.postgresql.api.PostgresqlConnection
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import manager.task.Context
import manager.task.domians.tables.Films
import manager.task.domians.tables.records.FilmsRecord
import org.jooq.DSLContext
import reactor.core.publisher.Flux


class DataBaseWarden() {

    /**
     * https://github.com/mp911de/r2dbc-postgres-notification-example/blob/main/src/main/java/com/example/demo/DemoApplication.java
     *
     * Живой пример работы с postgresql listener/notification
     *
     */

    val dslContext: DSLContext = Context.dbConfiguration.dbDsl
    val filmSubscribers = Context.filmsSubscriber
    val connection: PostgresqlConnection = Context.dbConfiguration.conFactory as PostgresqlConnection


    val filmActions = Flux.from(
        dslContext.selectFrom(Films.FILMS)
    )
        .collectList()
        .doOnNext { sendListTo(it) }
        .subscribe()

    fun sendListTo(list: List<FilmsRecord>) = runBlocking {
        launch {
            filmSubscribers.asIterable()
                .filter { it.session.isActive }
                .forEach {
                    it.session.outgoing.send(Frame.Text(list.toString()))
                }
        }
    }
}
