package lt.ltrp.player.vehicle.dao

import lt.ltrp.`object`.LtrpPlayer

/**
 * Created by Bebras on 2016-12-30.
 * DAO for [PlayerVehiclePermission]
 */
interface PlayerVehiclePermissionDao {

    fun get(): Collection<PlayerVehiclePermissionDao>
    fun get(ltrpPlayer: LtrpPlayer): Collection<PlayerVehiclePermissionDao>

}