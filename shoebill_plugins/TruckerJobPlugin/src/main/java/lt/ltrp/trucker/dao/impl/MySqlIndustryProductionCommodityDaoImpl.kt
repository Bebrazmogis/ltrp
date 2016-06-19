package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.IndustryProduction
import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.trucker.dao.IndustryProductionCommodityDao
import lt.ltrp.trucker.data.IndustryProductionCommodity
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
*
 */
 class MySqlIndustryProductionCommodityDaoImpl(ds: DataSource): IndustryProductionCommodityDao {

    val dataSource = ds

    override fun get(industryProduction: IndustryProduction, type: IndustryProductionCommodityType): List<IndustryProductionCommodity> {
        val sql = "SELECT * FROM trucker_industry_production_commodities " +
                "LEFT JOIN trucker_commodities ON trucker_commodities.id = trucker_industry_production_commodities.commodity_id " +
                "WHERE production_id = ? AND type = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.setInt(1, industryProduction.getUUID())
            stmt.setString(2, type.name)
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val commodities = ArrayList<IndustryProductionCommodity>()
        while(r.next())
            commodities.add(resultToIndustryProductionCommodity(r))
        r.close()
        return commodities
    }

    override fun remove(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity) {
        val sql = "DELETE FROM trucker_industry_production_commodities WHERE production_id = ? AND commodity_id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, industryProduction.getUUID())
            stmt.setInt(2, commodity.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity) {
        val sql = "INSERT INTO trucker_industry_production_commodities (production_id, commodity_id, amount, type) VALUES(?, ?, ?, ?)"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, industryProduction.getUUID())
            stmt.setInt(2, commodity.getUUID())
            stmt.setInt(3, commodity.amount)
            stmt.setString(4, commodity.type.name)
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun update(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity) {
        val sql = "UPDATE trucker_industry_production_commodities amount = ?, type = ? WHERE commodity_id = ? AND production_id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, commodity.amount)
            stmt.setString(2, commodity.type.name)
            stmt.setInt(3, commodity.getUUID())
            stmt.setInt(4, industryProduction.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }


    private fun resultToIndustryProductionCommodity(r: ResultSet): IndustryProductionCommodity {
        return IndustryProductionCommodity(r.getInt("id"), r.getString("name"), r.getInt("amount"), IndustryProductionCommodityType.valueOf(r.getString("type")))
    }
}