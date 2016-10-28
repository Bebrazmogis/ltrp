package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.dao.IndustryCommodityDao
import lt.ltrp.trucker.dao.IndustryDao
import lt.ltrp.trucker.dao.IndustryProductionDao
import net.gtaun.shoebill.data.Location
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
public class MySqlIndustryDaoImpl(ds: DataSource, productionDao: MySqlProductionDaoImpl,
                                  industryCommodityDao: IndustryCommodityDao): IndustryDao {


    override val industryProductionDao: IndustryProductionDao = productionDao

    override val industryCommodityDao: IndustryCommodityDao = industryCommodityDao

    private val dataSource = ds

    override fun get(uuid: Int): Industry? {
        val sql = "SELECT * FROM trucker_industries WHERE id = ?"
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
        val c = resultToIndustry(r)
        r.close()
        return c
    }

    override fun get(): List<Industry> {
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
        val industries = ArrayList<Industry>()
        while(r.next()) {
            industries.add(resultToIndustry(r))
        }
        r.close()
        return industries
    }

    override fun getFull(): List<Industry> {
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
        val industries = ArrayList<Industry>()
        while(r.next()) {
            val industry = resultToIndustry(r)
            industry.productions.addAll(industryProductionDao.get(industry))
            industry.commodities.addAll(industryCommodityDao.get(industry))
            industries.add(industry)
        }
        r.close()
        return industries
    }

    override fun update(industry: Industry) {
        val sql = "UPDATE trucker_industries SET name = ?, x = ?, y = ?, z = ?, interior_id = ?, world_id = ? WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setString(1, industry.name)
            stmt.setFloat(2, industry.location.x)
            stmt.setFloat(3, industry.location.y)
            stmt.setFloat(4, industry.location.z)
            stmt.setInt(5, industry.location.interiorId)
            stmt.setInt(6, industry.location.worldId)
            stmt.setInt(7, industry.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }

    }

    override fun delete(industry: Industry) {
        val sql = "DELETE FROM trucker_industries WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, industry.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(industry: Industry): Int {
        val sql = "INSERT INTO trucker_industries (name, x, y, z, interior_id, world_id) VALUES (?, ?, ?, ?, ?, ?)"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val keys: ResultSet =  try {
            stmt.setString(1, industry.name)
            stmt.setFloat(2, industry.location.x)
            stmt.setFloat(3, industry.location.y)
            stmt.setFloat(4, industry.location.z)
            stmt.setInt(5, industry.location.interiorId)
            stmt.setInt(6, industry.location.worldId)
            stmt.execute()
            stmt.getGeneratedKeys()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        var uuid: Int = 0
        if(keys.next())
            uuid = keys.getInt(1)
        keys.close()
        return uuid
    }

    private fun resultToIndustry(r: ResultSet): Industry {
        return Industry(r.getInt("id"),
                r.getString("name"),
                Location(r.getFloat("x"),
                        r.getFloat("y"),
                        r.getFloat("z"),
                        r.getInt("interior_id"),
                        r.getInt("world_id"))
                )
    }

}