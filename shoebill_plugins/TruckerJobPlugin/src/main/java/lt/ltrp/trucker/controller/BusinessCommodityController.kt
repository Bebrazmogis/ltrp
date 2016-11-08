package lt.ltrp.trucker.controller

import lt.ltrp.constant.BusinessType
import lt.ltrp.trucker.dao.BusinessCommodityDao
import lt.ltrp.trucker.data.Cargo

/**
 * Created by Bebras on 2016-11-07.
 * This class provides a way of knowing which business requires which cargo
 * Probably directly interacting with the DAO
 */
class BusinessCommodityController(private val dao: BusinessCommodityDao) {

    init {
        instance = this
    }

    fun get(businessType: BusinessType): Cargo? {
        return dao.getCargo(businessType)
    }

    fun get(): Map<BusinessType, Cargo> {
        return dao.getCargo()
    }

    companion object {
        lateinit var instance: BusinessCommodityController
    }
}