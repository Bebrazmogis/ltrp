package lt.ltrp.player.settings.dao.impl

import lt.ltrp.`object`.PlayerData
import lt.ltrp.player.settings.dao.PlayerSettingsDao
import lt.ltrp.player.settings.data.PlayerSettings
import net.gtaun.util.event.EventManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-07.
 * MySQL implementation of [PlayerSettingsDao]
 */
class SqlPlayerSettingsDaoImpl(var dataSource: DataSource, eventManager: EventManager): PlayerSettingsDao {

    override fun get(player: PlayerData): PlayerSettings? {
        var sql = "SELECT * FROM player_settings WHERE player_id = ?"
        val properties = Properties()
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, player.UUID)
            result = stmt.executeQuery()
            while(result.next())
                properties.put(result.getString("setting"), result.getString("value"))
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return PlayerSettings(player, properties)
    }

    override fun insert(settings: PlayerSettings) {
        var sql = "INSERT INTO player_settings (player_id, setting, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            settings.toProperties().forEach({
                stmt?.setInt(1, settings.player.UUID)
                stmt?.setString(2, it.key as String?)
                stmt?.setString(3, it.value as String?)
                stmt?.execute()
            })
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun update(settings: PlayerSettings) {
        var sql = "UPDATE player_settings SET value = ? WHERE player_id = ? AND setting = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            settings.toProperties().forEach({
                stmt?.setString(1, it.value as String?)
                stmt?.setInt(2, settings.player.UUID)
                stmt?.setString(3, it.key as String?)
                stmt?.execute()
            })
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

}
