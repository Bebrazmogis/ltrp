package lt.ltrp.player.vehicle.data

import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.player.vehicle.`object`.PlayerVehicle
import lt.ltrp.vehicle.permissions.data.VehiclePermission

/**
 * Created by Bebras on 2017-01-02.
 * This class represents a permission for a [PlayerVehicle]
 */
data class PlayerVehiclePermission(val playerData: PlayerData, val vehicle: PlayerVehicle,
                              val permission: VehiclePermission) {
}