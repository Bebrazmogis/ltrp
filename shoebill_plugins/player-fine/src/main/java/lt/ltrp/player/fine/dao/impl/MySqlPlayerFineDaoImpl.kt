package lt.ltrp.player.fine.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.player.PlayerController
import lt.ltrp.player.fine.dao.PlayerFineDao
import lt.ltrp.player.fine.data.PlayerFine
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-28.
 * MySQL implementation of the [PlayerFineDao] interface
 */

class MySqlPlayerFineDaoImpl(private val dataSource: DataSource): PlayerFineDao {

    override fun get(playerData: PlayerData): Collection<PlayerFine> {
        val sql = "SELECT * FROM player_fines WHERE player_id = ?"
        val fines = mutableSetOf<PlayerFine>()
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, playerData.UUID)
            result = stmt.executeQuery()
            while(result.next()) {
                fines.add(toFine(result, playerData))
            }
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return fines
    }

    private fun toFine(r: ResultSet, player: PlayerData): PlayerFine {
        return PlayerFine(
                r.getInt("id"),
                player,
                PlayerController.instance.getData(r.getInt("issued_by")) as PlayerData,
                r.getString("description"),
                r.getInt("fine"),
                r.getBoolean("is_paid"),
                r.getTimestamp("created_at").toLocalDateTime()
        )
    }

    override fun insert(playerFine: PlayerFine): Int {
        val sql = "INSERT INTO player_fines (player_id, issued_by, description, fine, is_paid, created_at) VALUES (?, ?, ?, ?, ?, ?)"
        var uuid = Entity.INVALID_ID
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, playerFine.player.UUID)
            stmt.setInt(2, playerFine.issuedBy.UUID)
            stmt.setString(3, playerFine.description)
            stmt.setInt(4, playerFine.fine)
            stmt.setBoolean(5, playerFine.isPaid)
            stmt.setTimestamp(6, Timestamp.valueOf(playerFine.createdAt))
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next()) {
                uuid = keys.getInt(1)
            }
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    override fun update(playerFine: PlayerFine) {
        val sql = "UPDATE player_fines SET description = ?, fine = ?, created_at = ?, is_paid = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setString(1, playerFine.description)
            stmt.setInt(2, playerFine.fine)
            stmt.setTimestamp(3, Timestamp.valueOf(playerFine.createdAt))
            stmt.setBoolean(4, playerFine.isPaid)
            stmt.setInt(5, playerFine.UUID)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun remove(playerFine: PlayerFine) {
        val sql = "DELETE FROM player_fines WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, playerFine.UUID)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }
}