package lt.ltrp.`object`.impl

import lt.ltrp.`object`.PlayerData
import lt.ltrp.constant.JobProperty
import lt.ltrp.job.JobController
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.FactionRank
import lt.ltrp.job.`object`.JobVehicle
import net.gtaun.util.event.EventManager


/**
 * @author Bebras
 *         2015.12.06.
 */
abstract class AbstractFaction(uuid: Int,
                               override val ranks: MutableList<out FactionRank>,
                               override var budget: Int,
                               @JobProperty("is_chat_enabled") override var isChatEnabled: Boolean,
                               private var leaderCollection: MutableCollection<PlayerData>,
                      eventManager: EventManager):
        AbstractJob(uuid, eventManager), Faction {

    constructor(uuid: Int, eventManager: EventManager):
        this(uuid, mutableListOf<FactionRank>(), 0, false, mutableSetOf<PlayerData>(), eventManager) {

    }

    override fun getVehicles(rank: FactionRank): kotlin.collections.Collection<JobVehicle> {
        return getVehicles(rank)
    }

    override fun getRankByNumber(number: Int): FactionRank? {
        return ranks.firstOrNull { it.number == number }
    }

    override fun getRankByUUID(uuid: Int): FactionRank? {
        return ranks.firstOrNull { it.UUID == uuid }
    }

    override fun getLeaders(): Collection<PlayerData> {
        return leaderCollection
    }

    override fun addLeader(playerData: PlayerData) {
        if(!leaderCollection.contains(playerData)) {
            leaderCollection.add(playerData)
            JobController.instance.addLeader(this, playerData)
        }
    }

    override fun removeLeader(playerData: PlayerData) {
        if(leaderCollection.contains(playerData)) {
            leaderCollection.remove(playerData)
            JobController.instance.removeLeader(this, playerData)
        }
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is Faction && other.UUID == this.UUID
    }

    override fun toString(): String {
        return super.toString() +
                String.format("Faction. Id %s Name:%s JobRank count:%d vehicle count:%d",
                        UUID, name, ranks.size, vehicles.size)
    }

    override fun hashCode(): Int {
        var result = ranks.hashCode()
        result = 31 * result + budget
        result = 31 * result + isChatEnabled.hashCode()
        result = 31 * result + leaderCollection.hashCode()
        return result
    }

}
