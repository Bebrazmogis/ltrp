package lt.ltrp.player.job.data

import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.`object`.JobRank

/**
 * @author Bebras
 *         2016.04.14.
 */
class PlayerJobData(
        val player: PlayerData,
        var job: Job,
        var rank: JobRank,
        var level: Int,
        var xp: Int,
        var hours: Int,
        var remainingContract: Int
        ) {

    constructor(player: PlayerData, job: Job, rank: JobRank): this(player, job, rank, 0, 0, 0, 0) {

    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}
