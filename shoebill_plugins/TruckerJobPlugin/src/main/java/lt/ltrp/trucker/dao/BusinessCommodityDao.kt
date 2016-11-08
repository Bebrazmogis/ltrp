package lt.ltrp.trucker.dao

import lt.ltrp.constant.BusinessType
import lt.ltrp.trucker.data.Cargo

/**
 * Created by Bebras on 2016-11-07.
 * Methods business commodity data
 * Basically it's just which business type requires which commodity
 */
interface BusinessCommodityDao {

    fun getCargo(businessType: BusinessType): Cargo?
    fun getCargo(): Map<BusinessType, Cargo>
    fun getBusinessType(cargo: Cargo)


}