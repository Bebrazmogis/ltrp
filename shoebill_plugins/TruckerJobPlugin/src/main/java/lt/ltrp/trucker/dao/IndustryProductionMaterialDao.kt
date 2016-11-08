package lt.ltrp.trucker.dao

import lt.ltrp.trucker.`object`.IndustryProduction
import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.trucker.data.IndustryProductionMaterial

/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryProductionMaterialDao {

    fun get(industryProduction: IndustryProduction, type: IndustryProductionCommodityType): List<IndustryProductionMaterial>
    fun remove(material: IndustryProductionMaterial)
    fun insert(material: IndustryProductionMaterial): Int
    fun update(material: IndustryProductionMaterial)
}