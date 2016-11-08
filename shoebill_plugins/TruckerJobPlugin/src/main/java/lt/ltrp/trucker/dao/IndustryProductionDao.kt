package lt.ltrp.trucker.dao

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.`object`.IndustryProduction

/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryProductionDao {

    val industryProductionCommodityDao: IndustryProductionMaterialDao

    fun get(industry: Industry): List<IndustryProduction>
    fun insert(industryProduction: IndustryProduction): Int
    fun update(industryProduction: IndustryProduction)
    fun remove(industryProduction: IndustryProduction)
}