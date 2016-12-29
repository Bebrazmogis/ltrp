package lt.ltrp.vehicle.dao

import lt.ltrp.vehicle.`object`.LtrpVehicle

/**
 * Created by Bebras on 2016-12-29.
 * An implementation of [VehicleDao] for simple, belonging to nobody vehicles
 */
interface StaticVehicleDao : VehicleDao {

    fun insert(ltrpVehicle: LtrpVehicle) : Boolean
    fun get(uuid: Int): LtrpVehicle?
    fun get(): Collection<LtrpVehicle>
    fun update(ltrpVehicle: LtrpVehicle): Boolean
    fun delete(ltrpVehicle: LtrpVehicle): Boolean

}