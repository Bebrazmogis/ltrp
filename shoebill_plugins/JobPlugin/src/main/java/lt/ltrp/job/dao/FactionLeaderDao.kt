package lt.ltrp.job.dao

import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.job.`object`.Faction

/**
 * Created by Bebras on 2016-10-25.
 * Methods for creating/destroying faction leaders
 */
interface FactionLeaderDao {

    /**
     * Adds a new leader to a faction. Note that it does not remove any previous leaders.
     * @param faction faction
     * *
     * @param userId leader UUID
     */
    fun insert(faction: Faction, userId: Int)

    /**
     * Removes the faction leader
     * @param faction faction
     * *
     * @param userId leader UUID
     */
    fun remove(faction: Faction, userId: Int)

    fun get(faction: Faction): Collection<PlayerData>
}