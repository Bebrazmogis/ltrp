package lt.ltrp.player.vehicle.`object`

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.player.vehicle.PlayerVehiclePlugin
import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission
import lt.ltrp.data.FuelTank
import lt.ltrp.data.VehicleLock
import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.util.event.EventManager

import java.util.*

/**
 * @author Bebras
 * *         2016.04.13.
 */
interface PlayerVehicle : LtrpVehicle {


    fun addPermission(userId: Int, permission: PlayerVehiclePermission)

    fun addPermission(player: LtrpPlayer, permission: PlayerVehiclePermission)

    /**
     * Returns the user permissions for this PlayerVehicle object
     * @param userId user ID whose permissions to get
     * *
     * @return a list of user permissions, if the user has no permission an empty collection is still returned
     */
    fun getPermissions(userId: Int): Collection<PlayerVehiclePermission>

    val permissions: Map<Int, Collection<PlayerVehiclePermission>>

    fun removePermission(player: LtrpPlayer, permission: PlayerVehiclePermission)

    fun removePermission(userId: Int, permission: PlayerVehiclePermission)

    fun removePermissions(userId: Int)

    var health: Float

    var insurance: Int

    var deaths: Int

    var alarm: VehicleAlarm

    var lock: VehicleLock

    var ownerId: Int

    companion object {

        fun get(): List<PlayerVehicle> {
            return PlayerVehiclePlugin.get(PlayerVehiclePlugin::class.java).getVehicles()
        }

        fun getByLicense(license: String): PlayerVehicle {
            return PlayerVehiclePlugin.get(PlayerVehiclePlugin::class.java).getByLicense(license)
        }

        fun getByVehicle(vehicle: Vehicle): PlayerVehicle {
            return PlayerVehiclePlugin.get(PlayerVehiclePlugin::class.java).getByVehicle(vehicle)
        }

        fun getByUniqueId(uniqueid: Int): PlayerVehicle? {
            val vehicle = LtrpVehicle.getByUniqueId(uniqueid)
            return if (vehicle != null && vehicle is PlayerVehicle) vehicle else null
        }

        fun getClosest(player: LtrpPlayer, maxDistance: Float): PlayerVehicle? {
            val op = get()
                    .stream()
                    .filter({ v -> v.getLocation().distance(player.location) < maxDistance })
                    .min({ v1, v2 -> java.lang.Float.compare(v1.getLocation().distance(player.location), v2.getLocation().distance(player.location)) })
            return if (op.isPresent()) op.get() else null
        }

        fun create(id: Int, modelId: Int, location: AngledLocation, color1: Int, color2: Int, ownerId: Int,
                   deaths: Int, fueltank: FuelTank, mileage: Float, license: String, insurance: Int, alarm: VehicleAlarm?,
                   lock: VehicleLock?, doors: Int, panels: Int, lights: Int, tires: Int, health: Float, eventManager: EventManager): PlayerVehicle {
            return PlayerVehiclePlugin.get(PlayerVehiclePlugin::class.java).createVehicle(id, modelId, location, color1, color2, ownerId, deaths, fueltank, mileage, license, insurance, alarm, lock, doors, panels, lights, tires, health, eventManager)
        }

        fun create(id: Int, modelId: Int, location: AngledLocation, color1: Int, color2: Int, ownerId: Int, fuelTank: FuelTank, mileage: Float, license: String, eventManager: EventManager): PlayerVehicle {
            return create(id, modelId, location, color1, color2, ownerId, 0, fuelTank, mileage, license, 0, null, null, 0, 0, 0, 0, 1000f, eventManager)
        }
    }

}
