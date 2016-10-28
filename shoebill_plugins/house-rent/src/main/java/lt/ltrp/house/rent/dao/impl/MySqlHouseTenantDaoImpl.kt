package lt.ltrp.house.rent.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.house.`object`.House
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import lt.ltrp.house.rent.`object`.HouseTenant
import lt.ltrp.house.rent.`object`.impl.HouseTenantImpl
import lt.ltrp.house.rent.dao.HouseTenantDao
import net.gtaun.util.event.EventManager
import java.sql.*
import java.time.ZoneOffset
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-06.
 *
 * MySQL implementation of the [HouseTenantDao] implementation
 */
class MySqlHouseTenantDaoImpl(var dataSource: DataSource, var eventManager: EventManager): HouseTenantDao {


    override fun get(house: House): List<HouseTenant> {
        val tenants = mutableListOf<HouseTenant>()
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement("SELECT * FROM house_tenants WHERE house_id = ?")
            stmt.setInt(1, house.UUID)
            result = stmt.executeQuery()
            while(result.next()) {
                tenants.add(resultToTenant(result, house))
            }
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return tenants
    }

    override fun get(player: LtrpPlayer): HouseTenant? {
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement("SELECT * FROM house_tenants WHERE player_id = ?")
            stmt.setInt(1, player.UUID)
            result = stmt.executeQuery()
            while(result.next()) {
                return resultToTenant(result)
            }
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return null
    }

    override fun insert(houseTenant: HouseTenant): Int {
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        var uuid = Entity.INVALID_ID
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement("INSERT INTO house_tenants (house_id, player_id, created_at) VALUES (?, ?, ?)")
            stmt.setInt(1, houseTenant.house.UUID)
            stmt.setInt(2, houseTenant.player.UUID)
            stmt.setTimestamp(3, Timestamp(houseTenant.rentTime.toEpochSecond(ZoneOffset.UTC)))
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next())
                uuid = keys.getInt(1)
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    override fun remove(houseTenant: HouseTenant) {
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement("DELETE FROM house_tenants WHERE id = ?")
            stmt.setInt(1, houseTenant.UUID)
            stmt.execute()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    private fun resultToTenant(r: ResultSet, house: House): HouseTenant {
        var playerData: PlayerData? = LtrpPlayer.get(r.getInt("player_id"))
        if(playerData == null) {
            playerData = PlayerData.get(r.getInt("player_id")) as PlayerData
        }
        return HouseTenantImpl(
                r.getInt("id"),
                playerData,
                house,
                r.getTimestamp("created_at").toLocalDateTime(),
                eventManager
        )
    }

    private fun resultToTenant(r: ResultSet): HouseTenant {
        return resultToTenant(r, House.get(r.getInt("house_id")))
    }
}