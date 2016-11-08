package lt.ltrp.trucker.dao.impl

import lt.ltrp.trucker.`object`.IndustryProduction
import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.trucker.dao.IndustryProductionMaterialDao
import lt.ltrp.`object`.Entity
import lt.ltrp.trucker.data.IndustryProductionMaterial
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.19.
*
 */
 class MySqlIndustryProductionMaterialDaoImpl(ds: DataSource, private val cargoDaoImpl: MySqlCargoDaoImpl):
        IndustryProductionMaterialDao {

    val dataSource = ds

    override fun get(industryProduction: IndustryProduction, type: IndustryProductionCommodityType): List<IndustryProductionMaterial> {
        val sql = "SELECT * FROM trucker_industry_production_materials " +
                "LEFT JOIN trucker_cargo ON trucker_cargo.id = trucker_industry_production_materials.cargo_id " +
                "WHERE production_id = ? AND type = ?"
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        val r: ResultSet = try {
            stmt.setInt(1, industryProduction.UUID)
            stmt.setString(2, type.name)
            stmt.executeQuery()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
        val commodities = ArrayList<IndustryProductionMaterial>()
        while(r.next())
            commodities.add(resultToIndustryProductionCommodity(r, industryProduction))
        r.close()
        return commodities
    }

    override fun remove(material: IndustryProductionMaterial) {
        val sql = "DELETE FROM trucker_industry_production_materials WHERE id = ?"
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, material.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }

    override fun insert(material: IndustryProductionMaterial): Int {
        val sql = "INSERT INTO trucker_industry_production_materials (production_id, cargo_id, amount, type) VALUES(?, ?, ?, ?)"
        var uuid = Entity.INVALID_ID
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        var keys: ResultSet? = null
        try {
            stmt.setInt(1, material.production.UUID)
            stmt.setInt(2, material.cargo.UUID)
            stmt.setInt(3, material.amount)
            stmt.setString(4, material.type.name)
            stmt.executeQuery()
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

    override fun update(material: IndustryProductionMaterial) {
        val sql = "UPDATE trucker_industry_production_materials amount = ?, type = ? WHERE id = ?"
        val con = dataSource.connection
        val stmt = con.prepareStatement(sql)
        try {
            stmt.setInt(1, material.amount)
            stmt.setString(2, material.type.name)
            stmt.setInt(3, material.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            throw e
        } finally {
            con.close()
            stmt.close()
        }
    }


    private fun resultToIndustryProductionCommodity(r: ResultSet, production: IndustryProduction): IndustryProductionMaterial {
        return IndustryProductionMaterial(r.getInt("id"),
                r.getInt("amount"),
                cargoDaoImpl.resultToCommodity(r),
                production,
                IndustryProductionCommodityType.valueOf(r.getString("type")))
    }
}