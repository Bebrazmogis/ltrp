package lt.ltrp.trucker.data

import lt.ltrp.trucker.constant.IndustryProductionCommodityType

/**
 * @author Bebras
* 2016.06.19.
*
*
* This commodity is used for production, it determines how MUCH of this commodity is needed for a certain production
 */
class IndustryProductionCommodity(uuid: Int, name:String, amount: Int, type: IndustryProductionCommodityType): Commodity(uuid, name) {

    var amount: Int = amount
        get
        set

    val type = type
        get
}