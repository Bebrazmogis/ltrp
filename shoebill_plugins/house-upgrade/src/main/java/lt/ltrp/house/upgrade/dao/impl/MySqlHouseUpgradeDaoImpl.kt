package lt.ltrp.house.upgrade.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.house.`object`.House
import lt.ltrp.house.upgrade.constant.HouseUpgradeType
import lt.ltrp.house.upgrade.dao.HouseUpgradeDao
import lt.ltrp.house.upgrade.data.HouseUpgrade
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-14.
 * A MySQL implementation of the [HouseUpgradeDao] interface
 */
class MySqlHouseUpgradeDaoImpl(private val dataSource: DataSource): HouseUpgradeDao {

    override fun get(house: House): Set<HouseUpgrade> {
        val sql = "SELECT * FROM house_upgrades WHERE house_id = ?"
        val upgrades = mutableSetOf<HouseUpgrade>()
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, house.UUID)
            result = stmt.executeQuery()
            while(result.next())
                upgrades.add(resultToUpgrade(result, house))
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return upgrades
    }

    override fun insert(upgrade: HouseUpgrade): Int {
        val sql = "INSERT INTO house_upgrades (house_id, upgrade_id) VALUES (?, ?)"
        var uuid = Entity.INVALID_ID
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, upgrade.house.UUID)
            stmt.setInt(2, upgrade.type.id)
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                uuid = keys.getInt(1)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    override fun remove(upgrade: HouseUpgrade) {
        val sql = "DELETE FROM house_upgrades WHERE house_id = ? AND upgrade_id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, upgrade.house.UUID)
            stmt.setInt(2, upgrade.type.id)
            stmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    private fun resultToUpgrade(r: ResultSet, house: House): HouseUpgrade {
        return HouseUpgrade(house, HouseUpgradeType.get(r.getInt("upgrade_id")))
    }
}