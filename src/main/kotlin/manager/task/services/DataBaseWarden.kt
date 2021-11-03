package manager.task.services


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.cio.websocket.*
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.postgresql.api.PostgresqlConnection
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import manager.task.Context
import manager.task.domains.DataBaseSubscribeResponse
import manager.task.domians.tables.records.FilmsRecord
import org.jooq.DSLContext
import org.slf4j.Logger
import reactor.core.publisher.Mono
import java.sql.Date
import java.time.LocalDate


class DataBaseWarden(val log: Logger) {


    /**
     *
     * Живой пример работы с postgresql listener/notification
     * https://github.com/mp911de/r2dbc-postgres-notification-example/blob/main/src/main/java/com/example/demo/DemoApplication.java
     *
     * Еще примеры, в том числе и с пояснениями изменений в БД
     * https://overcoder.net/q/229628/%D0%BA%D0%B0%D0%BA-%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B8%D1%82%D1%8C-%D0%B0%D1%81%D0%B8%D0%BD%D1%85%D1%80%D0%BE%D0%BD%D0%BD%D1%83%D1%8E-%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D1%8F%D0%B5%D0%BC%D1%83%D1%8E-%D1%81%D0%BE%D0%B1%D1%8B%D1%82%D0%B8%D1%8F%D0%BC%D0%B8-%D0%BF%D0%BE%D0%B4%D0%B4%D0%B5%D1%80%D0%B6%D0%BA%D1%83-listen-notify-%D0%B2-java-%D1%81
     *
     */

    val dslContext: DSLContext = Context.dbConfiguration.dbDsl
    val filmSubscribers = Context.filmsSubscriber
    val connection: PostgresqlConnection? = Mono
        .from(Context.dbConfiguration.conFactory.create())
        .cast(PostgresqlConnection::class.java).block()

    val objectMapper = ObjectMapper()
        .registerModule(KotlinModule())

    fun createListener() {
        connection!!.createStatement("LISTEN films_changed")
            .execute()
            .doOnNext {
                log.info("Establish psql listener connection")
            }
            .subscribe()

        connection
            .notifications
            .doOnNext { notify ->
                log.info("received notifications")
                notify.parameter?.let { data ->
                    objectMapper
                        .readValue<DataBaseSubscribeResponse>(data)
                        .also { sendListTo(it.tableName, it.value.toPrettyString()) }
                }
            }
            .subscribe();
    }

    private fun sendListTo(table: String, record: String) = runBlocking {
        launch {
            Context.mapSubscribers[table]?.let { set ->
                set.asIterable()
                    .filter { it.session.isActive }
                    .forEach {
                        it.session.outgoing.send(Frame.Text(record))
                    }
            }
        }
    }
}
