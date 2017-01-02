package lt.ltrp.vehicle.dao.impl

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.`object`.impl.LtrpVehicleImpl
import lt.ltrp.vehicle.dao.StaticVehicleDao
import lt.ltrp.vehicle.data.FuelTank
import net.gtaun.shoebill.constant.VehicleModel
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.util.event.EventManager
import org.slf4j.Logger
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-12-29.
 * MySQL implementation of [StaticVehicleDao]
 */
class MySqlStaticVehicleDaoImpl(dataSource: DataSource,
                                logger: Logger,
                                eventManager: EventManager) :
        StaticVehicleDao, AbstractMySqlVehicleDaoImpl(dataSource, eventManager, logger) {

    override fun insert(ltrpVehicle: LtrpVehicle): Boolean {
        val sql = "INSERT INTO static_vehicles (id, model_id, x, y, z, angle, interior_id, world_id, color1, color2, fuel_max, fuel, license, mileage)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        val vehicle = ltrpVehicle.vehicle ?: return false
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, ltrpVehicle.UUID)
            stmt.setInt(2, vehicle.modelId)
            stmt.setFloat(3, ltrpVehicle.spawnlocation.x)
            stmt.setFloat(4, ltrpVehicle.spawnlocation.y)
            stmt.setFloat(5, ltrpVehicle.spawnlocation.z)
            stmt.setFloat(6, ltrpVehicle.spawnlocation.angle)
            stmt.setInt(7, ltrpVehicle.spawnlocation.interiorId)
            stmt.setInt(8, ltrpVehicle.spawnlocation.worldId)
            stmt.setInt(9, vehicle.color1)
            stmt.setInt(10, vehicle.color2)
            stmt.setFloat(11, ltrpVehicle.fuelTank.size)
            stmt.setFloat(12, ltrpVehicle.fuelTank.fuel)
            stmt.setString(13, ltrpVehicle.license)
            stmt.setFloat(14, ltrpVehicle.mileage)
            return stmt.execute()
        } catch (e: SQLException) {
            logger.error("Error while inserting vehicle" + ltrpVehicle, e)
        } finally {
            connection?.close()
            stmt?.close()
        }
        return false
    }

    override fun get(uuid: Int): LtrpVehicle? {
        val sql = "SELECT * FROM static_vehicles WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, uuid)
            result = stmt.executeQuery()
            if(result.next()) {
                return toVehicle(result)
            }
        } catch (e: SQLException) {
            logger.error("Error retrieving vehicle " + uuid, e)
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return null
    }

    override fun get(): Collection<LtrpVehicle> {
        val vehicles = mutableListOf<LtrpVehicle>()
        val sql = "SELECT * FROM static_vehicles"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            result = stmt.executeQuery()
            while(result.next()) {
                vehicles.add(toVehicle(result))
            }
        } catch (e: SQLException) {
            logger.error("Error retrieving static vehicles", e)
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return vehicles
    }

    override fun update(ltrpVehicle: LtrpVehicle): Boolean {
        val sql = "UPDATE static_vehicles SET x = ?, y = ?, z = ?, angle = ?, interior_id = ?, world_id = ?, color1 = ?, color2 = ?, fuel_max = ?, fuel = ?, license = ?, mileage = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        val vehicle = ltrpVehicle.vehicle ?: return false
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setFloat(1, ltrpVehicle.spawnlocation.x)
            stmt.setFloat(2, ltrpVehicle.spawnlocation.y)
            stmt.setFloat(3, ltrpVehicle.spawnlocation.z)
            stmt.setFloat(4, ltrpVehicle.spawnlocation.angle)
            stmt.setInt(5, ltrpVehicle.spawnlocation.interiorId)
            stmt.setInt(6, ltrpVehicle.spawnlocation.worldId)
            stmt.setInt(7, vehicle.color1)
            stmt.setInt(8, vehicle.color2)
            stmt.setFloat(9, ltrpVehicle.fuelTank.size)
            stmt.setFloat(10, ltrpVehicle.fuelTank.fuel)
            stmt.setString(11, ltrpVehicle.license)
            stmt.setFloat(12, ltrpVehicle.mileage)
            stmt.setInt(13, ltrpVehicle.UUID)
            return stmt.execute()
        } catch (e: SQLException) {
            logger.error("Error updating vehicle " + ltrpVehicle, e)
        } finally {
            connection?.close()
            stmt?.close()
        }
        return false
    }

    override fun delete(ltrpVehicle: LtrpVehicle): Boolean {
        val sql = "DELETE FROM static_vehicles WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, ltrpVehicle.UUID)
            return stmt.execute()
        } catch (e: SQLException) {
            logger.error("Error deleting vehicle " + ltrpVehicle, e)
        } finally {
            connection?.close()
            stmt?.close()
        }
        return false
    }

    private fun toVehicle(r: ResultSet): LtrpVehicle {
        val vehicle = Vehicle.create(
                r.getInt("model_id"),
                r.getFloat("x"),
                r.getFloat("y"),
                r.getFloat("z"),
                r.getInt("interior_id"),
                r.getInt("world_id"),
                r.getFloat("angle"),
                r.getInt("color1"),
                r.getInt("color2"),
                -1,
                false)
        return LtrpVehicleImpl(
                r.getInt("id"),
                VehicleModel.get(r.getInt("model_id"))?.name + "-" + r.getInt("id"),
                vehicle,
                vehicle.location,
                FuelTank(r.getFloat("fuel_max"), r.getFloat("fuel")),
                false,
                r.getFloat("mileage"),
                r.getString("license"),
                eventManager
        )
    }
}