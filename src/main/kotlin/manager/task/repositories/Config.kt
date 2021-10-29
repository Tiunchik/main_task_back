package manager.task.repositories

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.util.*

class Config {

    val properties = javaClass.classLoader.getResourceAsStream("config.properties").use {
        Properties().apply { load(it) }
    }

    val conFactory: ConnectionFactory = ConnectionFactories.get(
        ConnectionFactoryOptions
            .parse(properties.getProperty("db.url"))
            .mutate()
            .option(ConnectionFactoryOptions.USER, properties.getProperty("db.username"))
            .option(ConnectionFactoryOptions.PASSWORD, properties.getProperty("db.password"))
            .build()
    )

    val dbDsl: DSLContext by lazy {
        DSL.using(
            conFactory
        )
    }

}
