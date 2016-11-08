package lt.ltrp.trucker.dao

import lt.ltrp.trucker.`object`.Industry
/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryDao {

    val productionDao: IndustryProductionDao
    val stockDao: IndustryStockDao

    fun get(uuid: Int): Industry?
    fun get(): List<Industry>
    fun getFull(): List<Industry>
    fun update(industry: Industry)
    fun delete(industry: Industry)
    fun insert(industry: Industry): Int


}