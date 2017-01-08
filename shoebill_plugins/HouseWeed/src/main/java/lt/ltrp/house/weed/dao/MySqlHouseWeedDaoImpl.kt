package lt.ltrp.house.weed.dao

import lt.ltrp.player.PlayerController
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.`object`.HouseWeedSapling
import lt.ltrp.house.weed.`object`.impl.HouseWeedSaplingImpl
import lt.ltrp.house.weed.constant.GrowthStage
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager
import java.sql.*
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-14.
 * A MySQL implementation of the [HouseWeedDao] interface
 */
class MySqlHouseWeedDaoImpl(private val dataSource: DataSource, private val eventManager: EventManager):
    HouseWeedDao {


    override fun getWeed(house: House): Set<HouseWeedSapling> {
        val weed = mutableSetOf<HouseWeedSapling>()
        val sql = "SELECT * FROM house_weed WHERE house_id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, house.UUID)
            result = stmt.executeQuery()
            while(result.next())
                weed.add(resultToWeed(result, house))
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return weed
    }

    override fun update(sapling: HouseWeedSapling) {
        val sql = "UPDATE house_weed SET x = ?, y = ?, z = ?, planted_at = ?, planted_by = ?,grown_at = ?, growth_stage = ?, harvested_by = ?, yield = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setFloat(1, sapling.location.x)
            stmt.setFloat(2, sapling.location.y)
            stmt.setFloat(3, sapling.location.z)
            stmt.setTimestamp(4, Timestamp.valueOf(sapling.plantedAt))
            stmt.setInt(5, sapling.plantedBy.UUID)
            if(sapling.grownAt != null)
                stmt.setTimestamp(6, Timestamp.valueOf(sapling.grownAt))
            else
                stmt.setNull(6, Types.TIMESTAMP)
            stmt.setInt(7, sapling.growthStage.ordinal)
            if(sapling.harvestedBy != null)
                stmt.setInt(8, sapling.harvestedBy!!.UUID)
            else
                stmt.setNull(8, Types.INTEGER)
            if(sapling.yieldAmount > 0) stmt.setInt(9, sapling.yieldAmount)
            else stmt.setNull(9, Types.INTEGER)
            stmt.setInt(10, sapling.UUID)
            stmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun insert(sapling: HouseWeedSapling) {
        val sql = "INSERT INTO house_weed (x, y, z, planted_at, planted_by, grown_at, growth_stage, harvested_by, yield) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setFloat(1, sapling.location.x)
            stmt.setFloat(2, sapling.location.y)
            stmt.setFloat(3, sapling.location.z)
            stmt.setTimestamp(4, Timestamp.valueOf(sapling.plantedAt))
            stmt.setInt(5, sapling.plantedBy.UUID)
            if(sapling.grownAt != null)
                stmt.setTimestamp(6, Timestamp.valueOf(sapling.grownAt))
            else
                stmt.setNull(6, Types.TIMESTAMP)
            stmt.setInt(7, sapling.growthStage.ordinal)
            if(sapling.harvestedBy != null)
                stmt.setInt(8, sapling.harvestedBy!!.UUID)
            else
                stmt.setNull(8, Types.INTEGER)
            if(sapling.yieldAmount > 0) stmt.setInt(9, sapling.yieldAmount)
            else stmt.setNull(9, Types.INTEGER)

            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                sapling.UUID = keys.getInt(1)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
    }

    override fun remove(weedSapling: HouseWeedSapling) {
        val sql = "DELETE FROM house_weed WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(10, weedSapling.UUID)
            stmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }


    private fun resultToWeed(r: ResultSet, house: House): HouseWeedSapling {
        val plantedById = r.getInt("planted_by")
        val plantedBy = LtrpPlayer.Companion.get(plantedById) ?: PlayerController.instance.getData(plantedById) ?: throw RuntimeException("Invalid player")
        var grownAt = r.getTimestamp("grown_at").toLocalDateTime()
        if(r.wasNull())
            grownAt = null
        val harvestedById = r.getInt("harvested_by")
        var harvestedBy: PlayerData? = null
        if(!r.wasNull())
            harvestedBy = LtrpPlayer.Companion.get(harvestedById) ?: PlayerController.instance.getData(harvestedById) ?: throw RuntimeException("invalid harvester")
        return HouseWeedSaplingImpl(r.getInt("id"),
                house,
                Location(r.getFloat("x"), r.getFloat("y"), r.getFloat("z"), house.exit.interiorId, house.exit.worldId),
                r.getTimestamp("planted_at").toLocalDateTime(),
                GrowthStage.values()[r.getInt("growth_stage")],
                plantedBy,
                grownAt,
                harvestedBy,
                r.getInt("yield"),
                eventManager
        )
    }

}