package lt.ltrp.trucker.data

import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.constant.IndustryStockType
import lt.ltrp.trucker.controller.IndustryStockController


/**
 * @author Bebras
 * 2016.06.19.
 *
 * This class represents an industry stock of a specific [cargo](TruckerCargo)
 * It basically provides information about the status of a cargo inside an industry: price, current stock and maximum stock
 */
open class IndustryStock(val industry: Industry,
                         uuid: Int,
                         val cargo: Cargo,
                         price: Int,
                         currentStock: Int,
                         maxStock: Int,
                         val type: IndustryStockType): EntityImpl(uuid)  {

    var price = price
        get
        set(value) {
            field = value
            IndustryStockController.INSTANCE.update(this)
        }

    var currentStock = currentStock
        get
        set(value) {
            field = value
            IndustryStockController.INSTANCE.update(this)
        }

    var maxStock = maxStock
        get
        set(value) {
            field = value
            IndustryStockController.INSTANCE.update(this)
        }
}