package lt.maze

import com.mchange.v2.c3p0.ComboPooledDataSource
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.Plugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

/**
 * Created by Bebras on 2016-12-24.
 * A plugin for opening a pooled connection to a sql database
 */

@ShoebillMain(name = "Database manager plugin",
        author = "Bebras",
        description = "Creates a datasource, thats all this plugin deoes.",
        version = "1.1")
class DatabasePlugin : Plugin() {

    lateinit var dataSource: ComboPooledDataSource

    override fun onEnable() {
        val logger = logger
        val ds = ComboPooledDataSource()
        try {
            ds.driverClass = driver
        } catch (e: Exception) {
            logger.error("Setting driver to \"" + driver + "\" failed. " + e.message, e.cause)
        }

        val props = ds.properties
        props.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.slf4j.Slf4jMLog")
        props.put("com.mchange.v2.log.NameTransformer", "com.mchange.v2.log.PackageNames")
        props.put("charSet", DEFAULT_CHARSET)
        ds.properties = props
        ds.jdbcUrl = url
        ds.user = user
        ds.password = password
        ds.minPoolSize = 1
        ds.maxPoolSize = 6

        var connection: Connection? = null
        var stmt: Statement? = null
        try {
            connection = ds.connection
            stmt = connection.createStatement()
            stmt.executeUpdate("SET NAMES " + DEFAULT_CHARSET)
            stmt.executeUpdate("SET character_set_client=" + DEFAULT_CHARSET)
            stmt.executeUpdate("SET character_set_results=" + DEFAULT_CHARSET)
            stmt.executeUpdate("SET character_set_connection=" + DEFAULT_CHARSET)
            stmt.executeUpdate("SET CHARACTER SET " + DEFAULT_CHARSET)
            stmt.executeUpdate("SET character_set_database=" + DEFAULT_CHARSET)
            stmt.executeUpdate("SET character_set_server=" + DEFAULT_CHARSET)

            val r = stmt.executeQuery("show variables like '%character%'")
            while (r.next()) {
                logger.debug(r.getObject(1).toString() + "=" + r.getObject(2))
            }
            logger.info("Charset successfully set to " + DEFAULT_CHARSET)
        } catch (e: SQLException) {
            logger.error("Could not set charset")
        } finally {
            connection?.close()
            stmt?.close()
        }

        dataSource = ds
        logger.info("Database plugin loaded. DataSource:" + dataSource.toString())
    }

    override fun onDisable() {
        logger.info("Shutting down..")
        dataSource.close()
    }

    @Throws(SQLException::class)
    fun query(query: String): Boolean {
        var connection: Connection? = null
        var stmt: Statement? = null
        try {
            connection = dataSource.connection
            stmt = connection.createStatement()
            return stmt.execute(query)
        } finally {
            connection?.close()
            stmt?.close()
        }

    }

    @Throws(SQLException::class, IOException::class)
    fun query(inputStream: InputStream): Boolean {
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use {
            return query(reader.readText())
        }
    }

    companion object {
        private val driver = "com.mysql.jdbc.Driver"
        private val user = "root"
        private val password = ""
        private val database = "ltrp-java"
        private val DEFAULT_CHARSET = "utf8"
        private val DEFAULT_COLLATE = "utf8_unicode_ci"
        private val url = "jdbc:mysql://localhost:3306/$database?characterEncoding=$DEFAULT_CHARSET"

    }
}