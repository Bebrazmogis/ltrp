package lt.ltrp.dao.impl

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.Entity
import lt.ltrp.player.licenses.constant.LicenseType
import lt.ltrp.player.licenses.dao.PlayerLicenseDao
import lt.ltrp.player.licenses.dao.PlayerLicenseWarningDao
import lt.ltrp.player.licenses.data.PlayerLicense
import lt.ltrp.player.licenses.data.PlayerLicenses
import java.sql.*
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-08.
 *
 * A MySQL implementation of the {@link lt.ltrp.player.licenses.dao.PlayerLicenseDao} interface
 */
class MySqlPlayerLicenseDaoImpl(val dataSource: DataSource, val licenseWarningDao: PlayerLicenseWarningDao):
        PlayerLicenseDao {

    override fun getAll(player: LtrpPlayer): PlayerLicenses {
        val sql = "SELECT * FROM player_licenses WHERE player_id = ?"
        var licenses = PlayerLicenses(player)
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, player.UUID)
            result = stmt.executeQuery()
            if(result.next()) {
                val license = toLicense(result, player)
                licenseWarningDao.getByLicense(license).forEach { license.addWarning(it) }
                licenses.add(license)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return licenses
    }

    override fun insert(license: PlayerLicense): Int {
        val sql = "INSERT INTO player_licenses (player_id, type, stage, created_at) VALUES (?, ?, ?, ?)"
        var uuid = Entity.INVALID_ID
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, license.player.UUID)
            stmt.setString(2, license.type.name)
            stmt.setInt(3, license.stage)
            stmt.setTimestamp(4, Timestamp.valueOf(license.dateAquired))
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                uuid = keys.getInt(1)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    override fun remove(playerLicense: PlayerLicense) {
        val sql = "DELETE FROM player_licenses WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, playerLicense.id)
            stmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun update(license: PlayerLicense) {
        val sql = "UPDATE player_licenses SET stage = ?, type = ?, created_at = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, license.stage)
            stmt.setString(2, license.type.name)
            stmt.setTimestamp(3, Timestamp.valueOf(license.dateAquired))
            stmt.setInt(4, license.id)
            stmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    private fun toLicense(r: ResultSet, p: LtrpPlayer): PlayerLicense {
        return PlayerLicense(r.getInt("id"),
                LicenseType.valueOf(r.getString("type")),
                r.getInt("stage"),
                r.getTimestamp("created_at").toLocalDateTime(),
                p)
    }
}