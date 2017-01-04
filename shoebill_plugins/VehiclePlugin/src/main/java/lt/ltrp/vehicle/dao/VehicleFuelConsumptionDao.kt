package lt.ltrp.vehicle.dao


/**
 * Created by Bebras on 2016-12-29.
 * A DAO for vehicle fuel consumption
 */
interface VehicleFuelConsumptionDao {

    fun get(vehicleModel: Int): Float
    fun update(vehicleModel: Int, consumption: Float)
    fun insert(vehicleModel: Int, consumption: Float)
    fun remove(vehicleModel: Int)

}