package lt.ltrp.trucker.dao

import lt.ltrp.trucker.`object`.IndustryProduction
import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.trucker.data.IndustryProductionCommodity

/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryProductionCommodityDao {

    fun get(industryProduction: IndustryProduction, type: IndustryProductionCommodityType): List<IndustryProductionCommodity>
    fun remove(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity)
    fun insert(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity)
    fun update(industryProduction: IndustryProduction, commodity: IndustryProductionCommodity)
}