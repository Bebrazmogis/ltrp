package lt.ltrp.trucker.controller.impl

import lt.ltrp.trucker.controller.IndustryStockController
import lt.ltrp.trucker.dao.IndustryStockDao
import lt.ltrp.trucker.data.IndustryStock

/**
 * Created by Bebras on 2016-10-29.
 */
class IndustryStockControllerImpl(private val stockDao: IndustryStockDao):
        IndustryStockController {

    override fun update(stock: IndustryStock) {
        stockDao.update(stock)
    }


}