package lt.ltrp.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.impl.FactionRankImpl
import lt.ltrp.dao.DaoException
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.FactionRank
import lt.ltrp.job.dao.FactionRankDao
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-26.
 */
class MySqlFactionRankDaoImpl(private val dataSource: DataSource): FactionRankDao {

    override fun get(job: Faction): Collection<FactionRank> {
        val ranks = mutableSetOf<FactionRank>()
        val sql = "SELET * FROM job_ranks WHERE job_id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var result: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, job.UUID)
            result = stmt.executeQuery()
            while(result.next()) {
                ranks.add(toRank(result, job))
            }
        } catch (e: SQLException) {
            throw DaoException("Could not retrieve ranks for job " + job.UUID, e)
        } finally {
            connection?.close()
            stmt?.close()
            result?.close()
        }
        return ranks
    }

    override fun update(rank: FactionRank) {
        val sql = "UPDATE job_ranks SET number = ?, job_id = ?, name = ?, salary = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, rank.number)
            stmt.setInt(2, rank.job.UUID)
            stmt.setString(3, rank.name)
            stmt.setInt(4, rank.salary)
            stmt.setInt(5, rank.UUID)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun insert(rank: FactionRank): Int {
        val sql = "INSERT INTO job_ranks (number, job_id, name, salary) VALUES (?, ?, ?, ?)"
        var uuid = Entity.INVALID_ID
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, rank.number)
            stmt.setInt(2, rank.job.UUID)
            stmt.setString(3, rank.name)
            stmt.setInt(4, rank.salary)
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

    override fun remove(rank: FactionRank) {
        val sql = "DELETE FROM job_ranks WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(sql)
            stmt.setInt(1, rank.UUID)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    private fun toRank(r: ResultSet, faction: Faction): FactionRank {
        return FactionRankImpl(
                r.getInt("id"),
                faction,
                r.getInt("number"),
                r.getString("name"),
                r.getInt("salary")
        )
    }
}