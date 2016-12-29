package lt.ltrp.vehicle

import lt.ltrp.`object`.Entity
import lt.ltrp.resource.DependentPlugin
import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.`object`.impl.LtrpVehicleImpl
import lt.ltrp.vehicle.dao.StaticVehicleDao
import lt.ltrp.vehicle.dao.VehicleFuelConsumptionDao
import lt.ltrp.vehicle.dao.impl.MySqlStaticVehicleDaoImpl
import lt.ltrp.vehicle.data.FuelTank
import lt.maze.DatabasePlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.constant.VehicleModel
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.shoebill.exception.CreationFailedException
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-12-29.
 * This plugin allows for managing static vehicles
 * A static vehicle is a simple SAMP vehicle, wrapped around in [LtrpVehicle] object
 * A static vehicle is nothing like a DMV, player or Job vehicle.
 * This plugin should add convenient ways to create such vehicles, but not actual UI to do so
 */

@ShoebillMain("Static vehicle plugin", "Bebras")
class StaticVehiclePlugin: DependentPlugin() {


    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var vehicleDao: StaticVehicleDao

    init {
        addDependency(DatabasePlugin::class)
        addDependency(VehiclePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDisable() {
        super.onDisable()
        eventManagerNode.cancelAll()
    }

    override fun onDependenciesLoaded() {
        val databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin::class.java)

        vehicleDao = MySqlStaticVehicleDaoImpl(databasePlugin!!.dataSource, logger, eventManagerNode)
        val vehicles = vehicleDao.get()
        logger.info("Loaded " + vehicles.size + " static vehicles")
    }

    fun create(modelId: Int, location: AngledLocation, color1: Color, color2: Color): LtrpVehicle? {
        val vehiclePlugin = ResourceManager.get().getPlugin(VehiclePlugin::class.java)
        val fuelDao: VehicleFuelConsumptionDao = vehiclePlugin?.getFuelConsumptionDao() ?: return null
        try {
            val vehicle = Vehicle.create(modelId, location, color1.value, color2.value, -1, false)
            val uuid = vehicleDao.insert()
            if(uuid != Entity.INVALID_ID) {
                val fuelTank = FuelTank(fuelDao.get(modelId), fuelDao.get(modelId))
                val name = VehicleModel.get(modelId)?.name + "-" + uuid
                val ltrpVehicle = LtrpVehicleImpl(uuid, name, vehicle, location, fuelTank, false, 0f, name, eventManagerNode)
                val success = vehicleDao.insert(ltrpVehicle)
                if(success) {
                    return ltrpVehicle
                } else {
                    ltrpVehicle.destroy()
                }
            }
        } catch(e: CreationFailedException) {
            logger.error("Failed to create a vehicle", e)
            return null
        }
        return null
    }

    fun update(ltrpVehicle: LtrpVehicle): Boolean {
        return vehicleDao.update(ltrpVehicle)
    }

    fun remove(ltrpVehicle: LtrpVehicle): Boolean {
        val success = vehicleDao.delete(ltrpVehicle)
        if(success) ltrpVehicle.destroy()
        return success
    }
}