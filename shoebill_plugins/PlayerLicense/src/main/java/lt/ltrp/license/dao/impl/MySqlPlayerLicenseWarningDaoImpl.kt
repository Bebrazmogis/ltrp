package lt.ltrp.license.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.player.licenses.dao.PlayerLicenseWarningDao
import lt.ltrp.player.licenses.data.LicenseWarning
import lt.ltrp.player.licenses.data.PlayerLicense
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-08.
 */
class MySqlPlayerLicenseWarningDaoImpl(var dataSource: DataSource): PlayerLicenseWarningDao {

    override fun get(uuid: Int): LicenseWarning? {
        var license: LicenseWarning? = null
        val sql = "SELECT * FROM player_license_warnings WHERE id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setInt(1, uuid)
            result = stmt.executeQuery()
            if(result.next())
                license = resultToLicense(result, null)
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return license
    }

    private fun  resultToLicense(result: ResultSet, playerLicense: PlayerLicense?): LicenseWarning {
        val warning = LicenseWarning()
        warning.id = result.getInt("id")
        warning.issuedBy = result.getString("issued_by")
        warning.date = result.getTimestamp("created_at").toLocalDateTime()
        warning.body = result.getString("warning")
        warning.license = playerLicense
        return warning
    }

    override fun getByLicense(playerLicense: PlayerLicense): Set<LicenseWarning> {
        val licenses = mutableSetOf<LicenseWarning>()
        val sql = "SELECT * FROM player_license_warnings WHERE license_id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setInt(1, playerLicense.id)
            result = stmt.executeQuery()
            while(result.next())
                licenses.add(resultToLicense(result, playerLicense))
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return licenses
    }

    override fun insert(licenseWarning: LicenseWarning): Int {
        val sql = "INSERT INTO player_license_warnings (license_id, warning, issued_by, created_at) VALUES (?, ?, ?, ?)"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        var id = Entity.INVALID_ID
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setInt(1, licenseWarning.license.id)
            stmt.setString(2, licenseWarning.body)
            stmt.setString(3, licenseWarning.issuedBy)
            stmt.setTimestamp(4, Timestamp.valueOf(licenseWarning.date))
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                id = keys.getInt(1)
        } finally {
            con?.close()
            stmt?.close()
            keys?.close()
        }
        licenseWarning.id = id
        return id
    }

    override fun remove(licenseWarning: LicenseWarning) {
        val sql = "DELETE FROM player_license_warnings WHERE id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setInt(1, licenseWarning.id)
            stmt.execute()
        } finally {
            con?.close()
            stmt?.close()
        }
    }

    override fun update(licenseWarning: LicenseWarning) {
        val sql = "UPDATE player_license_warnings SET warning = ?, issued_by = ? WHERE id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setString(1, licenseWarning.body)
            stmt.setString(2, licenseWarning.issuedBy)
            stmt.setInt(3, licenseWarning.id)
            stmt.execute()
        } finally {
            con?.close()
            stmt?.close()
        }
    }


}