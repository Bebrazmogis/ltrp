package lt.ltrp.job.dao

import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.FactionRank

/**
 * Created by Bebras on 2016-10-26.
 */
interface FactionRankDao {

    fun get(job: Faction): Collection<FactionRank>
    fun update(rank: FactionRank)
    fun insert(rank: FactionRank): Int
    fun remove(rank: FactionRank)
}