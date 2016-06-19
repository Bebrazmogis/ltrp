package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.`object`.IndustryProduction
import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.trucker.dao.IndustryProductionCommodityDao
import lt.ltrp.trucker.dao.IndustryProductionDao
import java.sql.ResultSet
import java.sql.SQLException
import java.util.ArrayList
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
public class MySqlProductionDaoImpl(ds: DataSource, comDao: MySqlIndustryProductionCommodityDaoImpl): IndustryProductionDao {

    val dataSource = ds
    override val industryProductionCommodityDao: IndustryProductionCommodityDao = comDao


    override fun get(industry: Industry): List<IndustryProduction> {
        val sql = "SELECT * FROM trucker_industry_productions WHERE industry_id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.setInt(1, industry.getUUID())
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val productions = ArrayList<IndustryProduction>()
        while(r.next()) {
            val prod = IndustryProduction(r.getInt("id"), industry)
            prod.producedCommodities.addAll(industryProductionCommodityDao.get(prod, IndustryProductionCommodityType.PRODUCT))
            prod.requiredCommodities.addAll(industryProductionCommodityDao.get(prod, IndustryProductionCommodityType.MATERIAL))
            productions.add(prod)
        }
        r.close()
        return productions
    }

    override fun insert(industryProduction: IndustryProduction): Int {
        val sql = "INSERT INTO trucker_industry_productions (industry_id) VALUES (?)"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val keys: ResultSet = try {
            stmt.setInt(1, industryProduction.industry.getUUID())
            stmt.execute()
            stmt.getGeneratedKeys()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        var uuid: Int = 0
        if(keys.next()) uuid = keys.getInt(1)
        keys.close()
        industryProduction.commodities().forEach {
            industryProductionCommodityDao.insert(industryProduction, it)
        }
        return uuid
    }

    override fun update(industryProduction: IndustryProduction) {
        val sql = "UPDATE trucker_industry_productions industry_id = ? WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, industryProduction.industry.getUUID())
            stmt.execute()
            stmt.getGeneratedKeys()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        industryProduction.commodities().forEach {
            industryProductionCommodityDao.update(industryProduction, it)
        }
    }

    override fun remove(industryProduction: IndustryProduction) {
        val sql = "DELETE FROM trucker_industry_productions WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, industryProduction.getUUID())
            stmt.execute()
            stmt.getGeneratedKeys()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        // Not necessary, foreign keys should take care of this
        /*industryProduction.commodities().forEach {
            industryProductionCommodityDao.remove(industryProduction, it)
        }*/
    }

}