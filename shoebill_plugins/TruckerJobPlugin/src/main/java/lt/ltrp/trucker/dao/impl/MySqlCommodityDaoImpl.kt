package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.dao.CommodityDao
import lt.ltrp.trucker.data.Commodity
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
public class MySqlCommodityDaoImpl(ds: DataSource): CommodityDao {

    private val dataSource = ds


    override fun get(uuid: Int): Commodity? {
        val sql = "SELECT * FROM trucker_commodities WHERE id = ?"
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

    override fun get(): List<Commodity> {
        val sql = "SELECT * FROM trucker_commodities"
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
        val c = ArrayList<Commodity>()
        while(r.next())
            c.add( resultToCommodity(r))
        r.close()
        return c
    }

    override fun update(commodity: Commodity) {
        val sql = "UPDATE trucker_commodities SET name = ? WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setString(1, commodity.name)
            stmt.setInt(2, commodity.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun delete(commodity: Commodity) {
        val sql = "DELETE FROM trucker_commodities WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, commodity.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(commodity: Commodity): Int {
        val sql = "INSERT INTO trucker_commodities (name) VALUES (?)"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val keys: ResultSet = try {
            stmt.setString(1, commodity.name)
            stmt.execute()
            stmt.getGeneratedKeys()
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

    private fun resultToCommodity(r: ResultSet): Commodity {
        return Commodity(r.getInt("id"), r.getString("name"))
    }
}