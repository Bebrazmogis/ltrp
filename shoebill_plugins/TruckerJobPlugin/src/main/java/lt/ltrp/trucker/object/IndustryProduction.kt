package lt.ltrp.trucker.`object`


import lt.ltrp.`object`.AbstractEntity
import lt.ltrp.trucker.data.IndustryProductionCommodity
import java.util.ArrayList

/**
 * @author Bebras
* 2016.06.19.
*
*
* This class represents a single production but it might required and/or produce multiple commodities
 */
class IndustryProduction(uuid: Int, industry: Industry): AbstractEntity(uuid) {

    var industry = industry
        get

     var requiredCommodities = ArrayList<IndustryProductionCommodity>()
         get
         private set

    var producedCommodities = ArrayList<IndustryProductionCommodity>()
        get
        private set

    fun commodities(): List<IndustryProductionCommodity> {
        val l = ArrayList<IndustryProductionCommodity>()
        l.addAll(requiredCommodities)
        l.addAll(producedCommodities)
        return l
    }

    fun canProduce(): Boolean {
        var can: Boolean = true
        requiredCommodities.forEach({
            if(it.amount > industry.commodities.first { it.equals(it) }.currentStock)
                can = false
        })
        producedCommodities.forEach({
            val com = industry.commodities.first{ it.equals(it) }
            val cStock: Int =  com.currentStock
            val maxStock = com.maxStock
            if(it.amount + cStock > maxStock)
                can = false
        })
        return can
    }

    fun produce(): Boolean {
        // TODO
        return canProduce()
    }

}