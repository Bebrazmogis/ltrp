package lt.ltrp.vehicle.`object`

import lt.ltrp.ActionMessenger
import lt.ltrp.StateMessenger
import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.NamedEntity
import lt.ltrp.vehicle.data.FuelTank
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Vehicle

/**
 * @author Bebras
 *         2016.04.12.
 */
interface LtrpVehicle: NamedEntity, StateMessenger, ActionMessenger, Destroyable {

    companion object {
        val SPEEC_MAGIC_NUMBER = 170.0f

        internal val vehicles: MutableList<LtrpVehicle> = mutableListOf()

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

        fun getClosest(player: Player, distance: Float) = getClosest(player.location, distance)
        fun getClosest(location: Location) = getClosest(location, Float.MAX_VALUE)
    }

    val vehicle: Vehicle?
    var spawnlocation: AngledLocation
    val fuelTank: lt.ltrp.vehicle.data.FuelTank

    val isSpawned: Boolean
        get() = vehicle != null
    var isLocked: Boolean
    var mileage: Float
    val speed: Float
    var license: String
    val isUsed: Boolean
    var driver: Player?
    var getLastDriver: Player?
    var getPullingVehicle: LtrpVehicle?

    fun isSeatWindowOpen(seatId: Int): Boolean
}
/*
public interface LtrpVehicle extends Vehicle, InventoryEntity {

    VehicleRadio getRadio(); // TODO new module
    TaxiFare getTaxi(); // TODO new module
}
*/