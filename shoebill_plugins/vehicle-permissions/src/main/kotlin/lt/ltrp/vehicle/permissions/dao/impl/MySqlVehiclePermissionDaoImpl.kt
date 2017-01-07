package lt.ltrp.vehicle.permissions.dao.impl

import lt.ltrp.vehicle.permissions.dao.VehiclePermissionDao
import lt.ltrp.vehicle.permissions.data.VehiclePermission
import org.slf4j.Logger
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2017-01-08.
 * A MySQL database implementation of [VehiclePermissionDao]
 */
class MySqlVehiclePermissionDaoImpl(private val dataSource: DataSource,
                                    private val logger: Logger) :
        VehiclePermissionDao {

    override fun get(): Collection<VehiclePermission> {
        val sql = "SELECT * FROM vehicle_permissions"
        val permissions = mutableSetOf<VehiclePermission>()
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            result = stmt.executeQuery()
            while(result.next()) {
                permissions.add(VehiclePermission(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("identifier")
                ))
            }
        } catch (e: SQLException) {
            logger.error("Could not retrieve vehicle permissions")
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return permissions
    }

    override fun insert(name: String, identifier: String): Int {
        val sql = "INSERT INTO vehicle_permissions (name, identifier) VALUES (?, ?)"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setString(1, name)
            stmt.setString(2, identifier)
            stmt.execute()
            result = stmt.generatedKeys
            if(result.next()) {
                return result.getInt(1)
            }
        } catch (e: SQLException) {
            logger.error("Could not insert vehicle permission[name=$name, identifier=$identifier]" , e)
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return 0
    }

    override fun remove(permission: VehiclePermission): Boolean {
        val sql = "DELETE FROM vehicle_permissions WHERE id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setInt(1, permission.uuid)
            return stmt.execute()
        } catch (e: SQLException) {
            logger.error("Could not remove vehicle permission $permission", e)
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return false
    }

    override fun update(permission: VehiclePermission): Boolean {
        val sql = "UPDATE vehicle_permissions SET name = ?, identifier = ? WHERE id = ?"
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setString(1, permission.name)
            stmt.setString(2, permission.identifier)
            stmt.setInt(3, permission.uuid)
            return stmt.execute()
        } catch (e: SQLException) {
            logger.error("Could not update vehicle permission $permission", e)
        } finally {
            con?.close()
            stmt?.close()
            result?.close()
        }
        return false
    }


}