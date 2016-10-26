package lt.ltrp.job.`object`


import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.NamedEntity
import lt.ltrp.job.JobController;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;


/**
 * @author Bebras
 *         2015.12.03.
 */
interface Job: NamedEntity {

    var location: Location
    val vehicles: MutableCollection<JobVehicle>
    var basePaycheck: Int
    val gates: MutableCollection<JobGate>
    val ranks:MutableList<out JobRank>

    fun getRankByNumber(number: Int): JobRank?
    fun getRankByUUID(uuid: Int): JobRank?

    fun getVehicles(rank: JobRank): Collection<JobVehicle>
    /**
     * Checks if a player is at a workplace
     * @param player
     * @return true if the player is considered to be at work, false otherwise
     */
    fun isAtWork(player: LtrpPlayer): Boolean


    /**
     * Sends a message to all connected players that belong to this job
     * @param color message color
     * @param message message text
     */
    fun sendMessage(color: Color, message: String)


    companion object {
        fun get(): List<Job> {
            return JobController.instance.get()
        }

        fun get(id: Int): Job? {
            return JobController.instance.get(id)
        }
    }


}

