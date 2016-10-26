package lt.ltrp.job.`object`

import lt.ltrp.`object`.PlayerData


/**
 * @author Bebras
 *         2016.04.14.
 */
interface Faction: Job {

    override val ranks: MutableList<out FactionRank>
    var budget: Int
    var isChatEnabled: Boolean

    fun getVehicles(rank: FactionRank): Collection<JobVehicle>
    override fun getRankByNumber(number: Int): FactionRank?
    override fun getRankByUUID(uuid: Int): FactionRank?
    fun getLeaders(): Collection<PlayerData>
    fun addLeader(playerData: PlayerData)
    fun removeLeader(playerData: PlayerData)
}
