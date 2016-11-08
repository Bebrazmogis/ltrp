package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.constant.TruckerCargoType
import lt.ltrp.trucker.dao.CargoDao
import lt.ltrp.trucker.data.Cargo
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
class MySqlCargoDaoImpl(ds: DataSource): CargoDao {

    private val dataSource = ds


    override fun get(uuid: Int): Cargo? {
        val sql = "SELECT * FROM trucker_cargo WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.setInt(1, uuid)
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val c = resultToCommodity(r)
        r.close()
        return c
    }

    override fun get(): List<Cargo> {
        val sql = "SELECT * FROM trucker_cargo"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val c = ArrayList<Cargo>()
        while(r.next())
            c.add( resultToCommodity(r))
        r.close()
        return c
    }

    override fun update(commodity: Cargo) {
        val sql = "UPDATE trucker_cargo SET name = ?, type = ? WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setString(1, commodity.name)
            stmt.setString(2, commodity.type.name)
            stmt.setInt(3, commodity.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun delete(commodity: Cargo) {
        val sql = "DELETE FROM trucker_cargo WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, commodity.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(commodity: Cargo): Int {
        val sql = "INSERT INTO trucker_cargo (name, type) VALUES (?, ?)"
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        val keys: ResultSet = try {
            stmt.setString(1, commodity.name)
            stmt.setString(2, commodity.type.name)
            stmt.execute()
            stmt.generatedKeys
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        var uuid = 0
        if(keys.next()) uuid = keys.getInt(1)
        keys.close()
        return uuid
    }

    internal fun resultToCommodity(r: ResultSet): Cargo {
        return Cargo(r.getInt("id"), r.getString("name"), TruckerCargoType.valueOf(r.getString("type")))
    }
}