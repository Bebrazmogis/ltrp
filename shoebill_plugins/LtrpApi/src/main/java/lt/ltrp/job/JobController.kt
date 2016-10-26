package lt.ltrp.job;

import lt.ltrp.`object`.PlayerData
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.Job
import lt.ltrp.player.job.data.PlayerJobData

/**
 * @author Bebras
 *         2016.04.13.
 */
abstract class JobController protected constructor() {

    init {
        instance = this
    }

    abstract fun get(): List<Job>
    abstract fun get(uuid: Int): Job?
    abstract fun getEmployeeList(job: Job): List<PlayerJobData>
    abstract fun update(job: Job)


    abstract fun addLeader(faction: Faction, playerData: PlayerData)
    abstract fun removeLeader(faction: Faction, playerData: PlayerData)

    companion object {
        lateinit var instance: JobController
    }
}
