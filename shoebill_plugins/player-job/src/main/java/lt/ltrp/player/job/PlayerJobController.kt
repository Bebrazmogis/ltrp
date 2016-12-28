package lt.ltrp.player.job

import lt.ltrp.`object`.PlayerData
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.`object`.JobRank
import lt.ltrp.player.job.data.PlayerJobData

/**
 * Created by Bebras on 2016-10-23.
 * This class defines all possible methods for
 * interacting with player job data from outside the module
 */
abstract class PlayerJobController protected constructor() {

    init {
        instance = this
    }

    abstract fun setJob(playerData: PlayerData, job: Job)
    abstract fun setJob(playerData: PlayerData, job: Job, rank: JobRank)
    abstract fun removeJob(playerData: PlayerData)
    abstract fun getData(playerData: PlayerData): PlayerJobData?
    abstract fun setRank(playerData: PlayerData, jobRank: JobRank)

    companion object {
        lateinit var instance: PlayerJobController
    }
}