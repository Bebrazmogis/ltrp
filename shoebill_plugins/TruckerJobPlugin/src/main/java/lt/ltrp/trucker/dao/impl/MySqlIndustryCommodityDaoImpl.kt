package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.constant.IndustryCommodityType
import lt.ltrp.trucker.dao.IndustryCommodityDao
import lt.ltrp.trucker.data.IndustryCommodity
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
public class MySqlIndustryCommodityDaoImpl(ds: DataSource): IndustryCommodityDao {

    val dataSource = ds

    override fun get(industry: Industry): List<IndustryCommodity> {
        val sql = "SELECT * FROM trucker_industry_commodities" +
                " LEFT JOIN trucker_commodities ON trucker_industry_commodities.commodity_id = trucker_commodities.id" +
                "WHERE trucker_industry_commodities.industry_id = ?"
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
        val commodities = ArrayList<IndustryCommodity>()
        while(r.next())
            commodities.add(resultToIndustryCommodity(industry, r))
        r.close()
        return commodities
    }

    override fun update(commodity: IndustryCommodity) {
        val sql = "UPDATE trucker_industry_commodities type = ?, price = ?, current_stock = ?, max_stock = ? WHERE industry_id = ? AND commodity_id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setString(1, commodity.type.name)
            stmt.setInt(2, commodity.price)
            stmt.setInt(3, commodity.currentStock)
            stmt.setInt(4, commodity.maxStock)
            stmt.setInt(5, commodity.industry.getUUID())
            stmt.setInt(6, commodity.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun remove(commodity: IndustryCommodity) {
        val sql = "DELETE FROM trucker_industry_commodities WHERE industry_id = ? AND commodity_id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, commodity.industry.getUUID())
            stmt.setInt(2, commodity.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(commodity: IndustryCommodity): Int {
        val sql = "UPDATE trucker_industry_commodities (industry_id, commodity_id, type, price, current_stock, max_stock) VALUES (?, ?, ?, ?, ?, ?)"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, commodity.industry.uuid)
            stmt.setInt(2, commodity.uuid)
            stmt.setString(3, commodity.type.name)
            stmt.setInt(4, commodity.price)
            stmt.setInt(5, commodity.currentStock)
            stmt.setInt(6, commodity.maxStock)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        return 0
    }

    private fun resultToIndustryCommodity(industry: Industry, r: ResultSet): IndustryCommodity {
        return IndustryCommodity(
                industry,
                r.getInt("trucker_industry_commodities.id"),
                r.getString("name"),
                IndustryCommodityType.valueOf(r.getString("type")),
                r.getInt("price"),
                r.getInt("current_stock"),
                r.getInt("max_stock")
        )
    }

}