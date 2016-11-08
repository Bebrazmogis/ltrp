package lt.ltrp.trucker.dao

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.constant.IndustryStockType
import lt.ltrp.trucker.data.IndustryStock

/**
 * @author Bebras
* 2016.06.19.
 */
interface IndustryStockDao {

    fun get(industry: Industry, type: IndustryStockType): List<IndustryStock>
    fun update(stock: IndustryStock)
    fun remove(stock: IndustryStock)
    fun insert(stock: IndustryStock): Int

}