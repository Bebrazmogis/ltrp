package lt.ltrp.player.vehicle.dao

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.player.vehicle.data.VehiclePermission

/**
 * Created by Bebras on 2016-12-30.
 * DAO for [VehiclePermission]
 */
interface VehiclePermissionDao {

    fun get(): Collection<VehiclePermission>

}