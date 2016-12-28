package lt.ltrp.job.`object`;

/**
 * @author Bebras
 *         2016.04.14.
 */
interface ContractJob: Job {

    override val ranks: MutableList<out ContractJobRank>
    var maxPaycheck: Int
    var contractLength: Int

    override fun getRankByNumber(number: Int): ContractJobRank?
    override fun getRankByUUID(uuid: Int): ContractJobRank?
    fun getVehicles(rank: ContractJobRank): Collection<JobVehicle>
}
