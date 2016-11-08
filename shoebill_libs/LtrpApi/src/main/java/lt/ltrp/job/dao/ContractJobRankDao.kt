package lt.ltrp.job.dao

import lt.ltrp.job.`object`.ContractJob
import lt.ltrp.job.`object`.ContractJobRank

/**
 * Created by Bebras on 2016-10-26.
 */
interface ContractJobRankDao {

    fun get(job: ContractJob): Collection<ContractJobRank>
    fun update(rank: ContractJobRank)
    fun insert(rank: ContractJobRank): Int
    fun remove(rank: ContractJobRank)
}