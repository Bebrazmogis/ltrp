package lt.ltrp.trucker.data

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.constant.IndustryCommodityType

/**
 * @author Bebras
* 2016.06.19.
*
* This type of commodity belongs to an industry
* It represents a single type of commodity
* {@link lt.ltrp.trucker.data.IndustryCommodity#type} determines whether the industry sells, or buys this type of commodity
 */
open class IndustryCommodity(industry: Industry, uuid: Int, name: String, type: IndustryCommodityType, price: Int, cStock: Int, maxStock: Int): Commodity(uuid, name) {

    var industry: Industry = industry
        get
        private set

    var type = type
        get
        set

    var price = price
        get
        set

    var currentStock = cStock
        get
        set

    var maxStock = maxStock
        get
        set

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is IndustryCommodity && other.type == type
    }
}