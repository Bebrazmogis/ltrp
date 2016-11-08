package lt.ltrp.trucker.data

import lt.ltrp.trucker.constant.IndustryProductionCommodityType
import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.`object`.IndustryProduction

/**
 * @author Bebras
* 2016.06.19.
*
*
* This commodity is used for production, it determines how MUCH of this [this#cargo] is needed for a certain production
 */
class IndustryProductionMaterial(uuid: Int,
                                 var amount: Int,
                                 val cargo: Cargo,
                                 val production: IndustryProduction,
                                 val type: IndustryProductionCommodityType):
    EntityImpl(uuid) {


}