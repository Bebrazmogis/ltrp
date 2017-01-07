package lt.ltrp.vehicle.permissions

import lt.ltrp.resource.DependentPlugin
import lt.ltrp.vehicle.permissions.dao.VehiclePermissionDao
import lt.ltrp.vehicle.permissions.dao.impl.MySqlVehiclePermissionDaoImpl
import lt.ltrp.vehicle.permissions.data.VehiclePermission
import lt.maze.DatabasePlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2017-01-08.
 *  This plugin provides permission management for vehicles
 *  Although these permissions are meant for vehicles they are not associated
 *  That association should be handled by sub implementations
 *
 */
@ShoebillMain("Vehicle permission plugin", "Bebras")
class VehiclePermissionPlugin: DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var permissionDao: VehiclePermissionDao

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDependenciesLoaded() {
        permissionDao = MySqlVehiclePermissionDaoImpl(
                ResourceManager.get().getPlugin(DatabasePlugin::class.java)?.dataSource!!, logger)
    }

    override fun onDisable() {
        super.onDisable()
        eventManagerNode.cancelAll()
        eventManagerNode.destroy()
    }

    fun get(): Collection<VehiclePermission> {
        return permissionDao.get()
    }

    fun update(vehiclePermission: VehiclePermission): Boolean {
        return permissionDao.update(vehiclePermission)
    }

    fun remove(vehiclePermission: VehiclePermission): Boolean {
        return permissionDao.remove(vehiclePermission)
    }

    fun create(name: String, identifier: String): VehiclePermission? {
        val uuid = permissionDao.insert(name, identifier)
        if(uuid != 0) {
            return VehiclePermission(uuid, name, identifier)
        } else {
            return null
        }
    }
}