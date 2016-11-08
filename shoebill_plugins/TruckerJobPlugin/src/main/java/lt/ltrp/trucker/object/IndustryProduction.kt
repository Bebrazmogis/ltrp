package lt.ltrp.trucker.`object`


import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.data.IndustryProductionMaterial
import java.util.ArrayList

/**
 * @author Bebras
* 2016.06.19.
*
*
* This class represents a single production but it might require and/or produce multiple commodities
 */
class IndustryProduction(uuid: Int, industry: Industry): EntityImpl(uuid) {

    var industry = industry
        get

     var requiredMaterials = ArrayList<IndustryProductionMaterial>()
         get
         private set

    var producedMaterials = ArrayList<IndustryProductionMaterial>()
        get
        private set

    fun commodities(): List<IndustryProductionMaterial> {
        val l = ArrayList<IndustryProductionMaterial>()
        l.addAll(requiredMaterials)
        l.addAll(producedMaterials)
        return l
    }

    fun canProduce(): Boolean {
        var can: Boolean = true
        requiredMaterials.forEach({ material ->
            if(material.amount > industry.boughtStock.first { material.cargo == it.cargo }.currentStock)
                can = false
        })
        // If the industry can not handle more products, it shouldn't produce
        producedMaterials.forEach({ material ->
            val com = industry.soldStock.first{ material.cargo == it.cargo }
            val cStock: Int =  com.currentStock
            val maxStock = com.maxStock
            if(material.amount + cStock > maxStock)
                can = false
        })
        return can
    }

    fun produce(): Boolean {
        // TODO
        return canProduce()
    }

}