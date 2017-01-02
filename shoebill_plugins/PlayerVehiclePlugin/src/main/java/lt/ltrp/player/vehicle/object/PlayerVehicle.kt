package lt.ltrp.player.vehicle.`object`

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.vehicle.PlayerVehiclePlugin
import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission
import lt.ltrp.data.FuelTank
import lt.ltrp.data.VehicleLock
import lt.ltrp.player.vehicle.PlayerVehicleContainer
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

        fun get(): Collection<PlayerVehicle> {
            return PlayerVehicleContainer.get()
        }

        fun get(uuid: Int): PlayerVehicle? {
            return get()
                    .firstOrNull { it.UUID == uuid }
        }

        fun getByLicense(license: String): PlayerVehicle? {
            return get()
                    .firstOrNull { it.license == license }
        }

        fun getByVehicle(vehicle: Vehicle): PlayerVehicle? {
            return get()
                    .firstOrNull { it.vehicle == vehicle }
        }

        @Deprecated("No longer used", ReplaceWith("get(uuid: Int)"))
        fun getByUniqueId(uniqueid: Int): PlayerVehicle? {
            return get(uniqueid)
        }

        fun getClosest(player: LtrpPlayer, maxDistance: Float): PlayerVehicle? {
            val closestVehicle = get()
                    .minBy { it.vehicle.location.distance(player.player.location) }
            val op = get()
                    .stream()
                    .filter({ v -> v.getLocation().distance(player.location) < maxDistance })
                    .min({ v1, v2 -> java.lang.Float.compare(v1.getLocation().distance(player.location), v2.getLocation().distance(player.location)) })
            return if (op.isPresent()) op.get() else null
        }
    }

}
