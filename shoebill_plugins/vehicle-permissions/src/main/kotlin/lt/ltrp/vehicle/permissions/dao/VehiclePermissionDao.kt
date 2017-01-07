package lt.ltrp.vehicle.permissions.dao

import lt.ltrp.vehicle.permissions.data.VehiclePermission

/**
 * Created by Bebras on 2016-12-30.
 * DAO for [VehiclePermission]
 */
interface VehiclePermissionDao {

    fun get(): Collection<VehiclePermission>
    fun insert(name: String, identifier: String): Int
    fun remove(permission: VehiclePermission): Boolean
    fun update(permission: VehiclePermission): Boolean

}