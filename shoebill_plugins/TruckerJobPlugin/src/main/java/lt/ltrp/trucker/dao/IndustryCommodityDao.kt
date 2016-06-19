package lt.ltrp.trucker.dao

import lt.ltrp.trucker.data.IndustryCommodity
import lt.ltrp.trucker.`object`.Industry

/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryCommodityDao {

    fun get(industry: Industry): List<IndustryCommodity>
    fun update(commodity: IndustryCommodity)
    fun remove(commodity: IndustryCommodity)
    fun insert(commodity: IndustryCommodity): Int

}