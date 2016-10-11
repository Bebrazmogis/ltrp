package lt.ltrp.dao.impl;

import lt.ltrp.dao.PlayerSettingsDao
import lt.ltrp.data.PlayerSettings
import net.gtaun.util.event.EventManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-07.
 */
class SqlPlayerSettingsDaoImpl(var dataSource: DataSource, eventManager: EventManager): PlayerSettingsDao {

    override fun update(settings: PlayerSettings) {
        var sql = "INSERT INTO player_settings (player_id, setting, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            settings.toProperties().forEach({
                stmt?.setInt(1, settings.player.uuid)
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

}
