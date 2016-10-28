package lt.ltrp

import lt.ltrp.`object`.PlayerData
import lt.ltrp.job.JobController
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.dao.FactionLeaderDao
import lt.ltrp.job.dao.JobDao
import lt.ltrp.player.job.data.PlayerJobData
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-25.
 *
 */
class JobControllerImpl(private var jobDao: JobDao,
                        private val leaderDao: FactionLeaderDao,
                        eventManager: EventManager):
        JobController() {


    override fun get(): List<Job> {
        return JobContainer.get()
    }

    override fun get(uuid: Int): Job? {
        return JobContainer.get().firstOrNull { it.UUID == uuid }
    }

    override fun getEmployeeList(job: Job): List<PlayerJobData> {
        return jobDao.getEmployees(job).toList()
    }

    override fun update(job: Job) {
        jobDao.update(job)
    }

    override fun addLeader(faction: Faction, playerData: PlayerData) {
        if(!faction.getLeaders().contains(playerData)) {
            leaderDao.insert(faction, playerData.UUID)
            faction.addLeader(playerData)
        }
    }

    override fun removeLeader(faction: Faction, playerData: PlayerData) {
        if(faction.getLeaders().contains(playerData)) {
            leaderDao.remove(faction, playerData.UUID)
            faction.removeLeader(playerData)
        }
    }
}