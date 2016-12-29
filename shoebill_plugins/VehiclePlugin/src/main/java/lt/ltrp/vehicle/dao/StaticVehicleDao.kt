package lt.ltrp.vehicle.dao

import lt.ltrp.`object`.LtrpVehicle

/**
 * Created by Bebras on 2016-12-29.
 * An implementation of [VehicleDao] for simple, belonging to nobody vehicles
 */
interface StaticVehicleDao : VehicleDao {

    fun insert(vehicle: LtrpVehicle) : Boolean
    fun get(uuid: Int): LtrpVehicle
    fun update(vehicle: LtrpVehicle): Boolean
    fun delete(vehicle: LtrpVehicle): Boolean

}