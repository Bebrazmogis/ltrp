package lt.ltrp.trucker.dao

import lt.ltrp.trucker.data.Commodity

/**
 * @author Bebras
* 2016.06.19.
 */
public interface CommodityDao {

    fun get(uuid: Int): Commodity?
    fun get(): List<Commodity>
    fun update(commodity: Commodity)
    fun delete(commodity: Commodity)
    fun insert(commodity: Commodity): Int

}