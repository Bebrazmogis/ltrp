package lt.ltrp.player.vehicle.data

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData

/**
 * Created by Bebras on 2017-01-02.
 * A container for [PlayerVehiclePermission] objects
 */
class PlayerVehiclePermissions(player: PlayerData) {

    val permissions = mutableSetOf<PlayerVehiclePermission>()

    fun contains(identifier: String): Boolean {
        return permissions.map { it.permission.identifier }.contains(identifier)
    }

    fun get(): Collection<VehiclePermission> {
        return permissions.map { it.permission }
    }
}