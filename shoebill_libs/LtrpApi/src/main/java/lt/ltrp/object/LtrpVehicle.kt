package lt.ltrp.`object`

import lt.ltrp.data.FuelTank
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Vehicle

/**
 * @author Bebras
 *         2016.04.12.
 */
interface LtrpVehicle: InventoryEntity {

    companion object {
        val SPEEC_MAGIC_NUMBER = 170.0

        protected val vehicles: MutableList<LtrpVehicle> = mutableListOf()

        fun get(): List<LtrpVehicle> {
            return vehicles
        }

        fun getById(uuid: Int): LtrpVehicle? {
            if(uuid == Entity.INVALID_ID) return null
            return vehicles.firstOrNull { it.UUID == uuid }
        }

        fun getByVehicle(vehicle: Vehicle): LtrpVehicle? {
            return vehicles.firstOrNull { it.vehicle == vehicle }
        }

        fun getClosest(location: Location, distance: Float): LtrpVehicle? {
            return vehicles
                    .filter { it.vehicle != null }
                    .filter { it.vehicle?.location?.distance(location)!! < distance }
                    .minBy { it.vehicle?.location?.distance(location)!! }
        }

        fun getClosest(player: LtrpPlayer, distance: Float) = getClosest(player.player.location, distance)
        fun getClosest(location: Location) = getClosest(location, Float.MAX_VALUE)
    }

    val vehicle: Vehicle?
    var spawnlocation: AngledLocation
    val fuelTank: FuelTank

    var isLocked: Boolean
    var mileage: Float
    val speed: Float
    var license: String
    val isUsed: Boolean
    var driver: LtrpPlayer?
    var getLastDriver: LtrpPlayer?
    var getPullingVehicle: LtrpVehicle?

    fun sendActionMessage(message: String, distance: Float)
    fun sendActionMessage(message: String) = sendActionMessage(message, 10f)

    fun sendStateMessage(message: String, distance: Float)
    fun sendStateMessage(message: String) = sendStateMessage(message, 10f)

    fun isSeatWindowOpen(seatId: Int): Boolean
}
/*
public interface LtrpVehicle extends Vehicle, InventoryEntity {

    VehicleRadio getRadio(); // TODO new module
    TaxiFare getTaxi(); // TODO new module
}
*/