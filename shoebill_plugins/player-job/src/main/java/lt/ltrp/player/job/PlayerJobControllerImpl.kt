package lt.ltrp.player.job

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.`object`.JobRank
import lt.ltrp.player.job.dao.PlayerJobDao
import lt.ltrp.player.job.data.PlayerJobData
import lt.ltrp.player.job.event.PlayerChangeJobEvent
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-24.
 * An implementation of the [PlayerJobController] class
 */
class PlayerJobControllerImpl(private val dao: PlayerJobDao, private val eventManager: EventManager):
        PlayerJobController() {

    override fun setJob(playerData: PlayerData, job: Job) {
        val rank = job.ranks.minBy { it.number }
        if(rank != null)
            setJob(playerData, job, rank)
    }

    override fun setJob(playerData: PlayerData, job: Job, rank: JobRank) {
        val oldJob = playerData.jobData?.job
        playerData.jobData = PlayerJobData(playerData, job, rank)
        dao.update(playerData.jobData)
        if(playerData is LtrpPlayer)
            eventManager.dispatchEvent(PlayerChangeJobEvent(playerData, oldJob, job))
    }

    override fun removeJob(playerData: PlayerData) {
        val oldData = playerData.jobData
        playerData.jobData = null
        if(playerData is LtrpPlayer) {
            val player = playerData as LtrpPlayer
            player.removeJobWeapons()
            eventManager.dispatchEvent(PlayerChangeJobEvent(player, oldData?.job, null))
        }
        if(oldData?.job is Faction)
            (oldData?.job as Faction).removeLeader(playerData)
    }

    override fun getData(playerData: PlayerData): PlayerJobData? {
        if(playerData.jobData != null)
            return playerData.jobData as PlayerJobData
        else
            return dao.get(playerData)
    }

    override fun setRank(playerData: PlayerData, jobRank: JobRank) {
        if(playerData.jobData != null) {
            playerData.jobData?.rank = jobRank
            dao.update(playerData.jobData)
        }
    }
}