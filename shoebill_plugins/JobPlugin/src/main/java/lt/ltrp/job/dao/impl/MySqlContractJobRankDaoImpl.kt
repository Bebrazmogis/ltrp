package lt.ltrp.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.impl.ContractJobRankImpl
import lt.ltrp.dao.DaoException
import lt.ltrp.job.`object`.ContractJob
import lt.ltrp.job.`object`.ContractJobRank
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.dao.ContractJobRankDao
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-26.
 */
class MySqlContractJobRankDaoImpl(private val dataSource: DataSource): ContractJobRankDao {

    override fun get(job: ContractJob): Collection<ContractJobRank> {
        val ranks = mutableSetOf<ContractJobRank>()
        val sql = "SELECT * FROM job_ranks LEFT JOIN job_ranks_contract ON job_ranks_contract WHERE job_id = ?"
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

    override fun update(rank: ContractJobRank) {
        val rankSql = "UPDATE job_ranks SET number = ?, job_id = ?, name = ?, salary = ? WHERE id = ?"
        val cRankSql = "UPDATE job_ranks_contract SET xp_needed = ? WHERE id = ?"
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(rankSql)
            stmt.setInt(1, rank.number)
            stmt.setInt(2, rank.job.UUID)
            stmt.setString(3, rank.name)
            stmt.setInt(4, rank.salary)
            stmt.setInt(5, rank.UUID)
            stmt.execute()

            stmt?.close()
            stmt = connection.prepareStatement(cRankSql)
            stmt.setInt(1, rank.xpNeeded)
            stmt.setInt(2, rank.UUID)
            stmt.execute()
        } finally {
            connection?.close()
            stmt?.close()
        }
    }

    override fun insert(rank: ContractJobRank): Int {
        val rankSql = "INSERT INTO job_ranks (number, job_id, name, salary) VALUES (?, ?, ?, ?)"
        val cRankSql = "INSERT INTO job_ranks_contract (ip, xp_needed) VALUES (?, ?)"
        var uuid = Entity.INVALID_ID
        var connection: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            connection = dataSource.connection
            stmt = connection.prepareStatement(rankSql)
            stmt.setInt(1, rank.number)
            stmt.setInt(2, rank.job.UUID)
            stmt.setString(3, rank.name)
            stmt.setInt(4, rank.salary)
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next()) {
                uuid = keys.getInt(1)
                stmt.close()
                stmt = connection.prepareStatement(cRankSql)
                stmt.setInt(1, uuid)
                stmt.setInt(2, rank.xpNeeded)
                stmt.execute()
            }
        } finally {
            connection?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    override fun remove(rank: ContractJobRank) {
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

    private fun toRank(r: ResultSet, job: ContractJob): ContractJobRank {
        return ContractJobRankImpl(
                r.getInt("id"),
                job,
                r.getInt("number"),
                r.getInt("xp_needed"),
                r.getString("name"),
                r.getInt("salary")
        )
    }
}