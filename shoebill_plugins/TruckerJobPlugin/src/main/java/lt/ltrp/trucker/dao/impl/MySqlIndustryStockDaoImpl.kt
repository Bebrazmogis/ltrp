package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.`object`.Entity
import lt.ltrp.trucker.constant.IndustryStockType
import lt.ltrp.trucker.dao.IndustryStockDao
import lt.ltrp.trucker.data.IndustryStock
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
 */
class MySqlIndustryStockDaoImpl(ds: DataSource, private val cargoDao: MySqlCargoDaoImpl): IndustryStockDao {

    val dataSource = ds

    override fun get(industry: Industry, type: IndustryStockType): List<IndustryStock> {
        val sql = "SELECT * FROM trucker_industry_stock" +
                " LEFT JOIN trucker_cargo ON trucker_cargo.id = trucker_industry_stock.cargo_id" +
                "WHERE trucker_industry_stock.industry_id = ? AND type = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.setInt(1, industry.UUID)
            stmt.setString(2, type.name)
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val commodities = ArrayList<IndustryStock>()
        while(r.next())
            commodities.add(resultToIndustryCommodity(industry, r))
        r.close()
        return commodities
    }

    override fun update(stock: IndustryStock) {
        val sql = "UPDATE trucker_industry_stock price = ?, current_stock = ?, max_stock = ? WHERE id = ?"
        val con = dataSource.getConnection()
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, stock.price)
            stmt.setInt(2, stock.currentStock)
            stmt.setInt(3, stock.maxStock)
            stmt.setInt(5, stock.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun remove(stock: IndustryStock) {
        val sql = "DELETE FROM trucker_industry_stock WHERE id = ?"
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, stock.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(stock: IndustryStock): Int {
        val sql = "UPDATE trucker_industry_stock (industry_id, cargo_id, price, current_stock, max_stock, type) VALUES (?, ?, ?, ?, ?, ?)"
        var uuid = Entity.INVALID_ID
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        var keys: ResultSet? = null
        try {
            stmt.setInt(1, stock.industry.UUID)
            stmt.setInt(2, stock.cargo.UUID)
            stmt.setInt(3, stock.price)
            stmt.setInt(4, stock.currentStock)
            stmt.setInt(5, stock.maxStock)
            stmt.setString(6, stock.type.name)
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                uuid = keys.getInt(1)
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
            keys?.close()
        }
        return uuid
    }

    private fun resultToIndustryCommodity(industry: Industry, r: ResultSet): IndustryStock {
        return IndustryStock(
                industry,
                r.getInt("trucker_industry_stock.id"),
                cargoDao.resultToCommodity(r),
                r.getInt("price"),
                r.getInt("current_stock"),
                r.getInt("max_stock"),
                IndustryStockType.valueOf(r.getString("type"))
        )
    }

}