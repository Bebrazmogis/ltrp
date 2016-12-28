package lt.ltrp.job.`object`;

import lt.ltrp.`object`.Entity
import kotlin.properties.Delegates

/**
 * @author Bebras
 *         2015.12.06.
 */
interface JobRank: Entity {

    var number: Int
    var name: String
    val job: Job
    var salary: Int

    operator fun compareTo(jobRank: JobRank): Int {
        return number.compareTo(jobRank.number)
    }

    override fun equals(other: Any?): Boolean
}
