package lt.ltrp.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.dao.FactionLeaderDao
import lt.ltrp.player.PlayerController
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-26.
 */
class MySqlFactionLeaderDaoImpl(private val dataSource: DataSource): FactionLeaderDao {
    override fun insert(faction: Faction, userId: Int) {
        val sql = "INSERT INTO job_leaders (job_id, player_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP())"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, faction.UUID)
            stmt.setInt(2, userId)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun remove(faction: Faction, userId: Int) {
        val sql = "DELETE FROM job_leaders WHERE job_id = ? AND player_id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, faction.UUID)
            stmt.setInt(2, userId)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun get(faction: Faction): Collection<PlayerData> {
        val leaders = mutableSetOf<PlayerData>()
        val sql = "SELECT * FROM job_leaders WHERE job_id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, faction.UUID)
            result = stmt.executeQuery()
            while(result.next()) {
                val player = PlayerController.get().getData(result.getInt("player_id"))
                if(player != null)
                    leaders.add(player)
            }
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return leaders
    }
}