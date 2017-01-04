package lt.ltrp.player.vehicle.dao

import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.vehicle.`object`.PlayerVehicle

/**
 * @author Bebras
 * *         2016.05.31.
 */
interface PlayerVehiclePermissionDao {


    fun insert()

    fun remove(vehicle: PlayerVehicle, userId: Int)
    fun remove(vehicle: PlayerVehicle)
    fun remove(vehicle: PlayerVehicle, userId: Int, permission: PlayerVehiclePermission)
    fun add(vehicle: PlayerVehicle, player: LtrpPlayer, permission: PlayerVehiclePermission)
    fun add(vehicle: PlayerVehicle, userId: Int, permission: PlayerVehiclePermission)
    fun add(vehicleId: Int, userId: Int, permission: PlayerVehiclePermission)
    operator fun get(vehicleId: Int, userId: Int): Collection<PlayerVehiclePermission>
    operator fun get(vehicleId: Int): Map<Int, PlayerVehiclePermission>

}
